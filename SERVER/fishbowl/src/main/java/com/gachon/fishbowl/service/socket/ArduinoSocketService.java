package com.gachon.fishbowl.service.socket;

import com.gachon.fishbowl.entity.DeviceId;
import com.gachon.fishbowl.entity.Sensing;
import com.gachon.fishbowl.entity.UserDevice;
import com.gachon.fishbowl.entity.UserSet;
import com.gachon.fishbowl.repository.DeviceIdRepository;
import com.gachon.fishbowl.repository.SensingRepository;
import com.gachon.fishbowl.repository.UserDeviceRepository;
import com.gachon.fishbowl.repository.UserSetRepository;
import com.gachon.fishbowl.service.FirebaseService;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.transaction.Transactional;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Component
@Transactional
public class ArduinoSocketService extends TextWebSocketHandler {

    private static List<WebSocketSession> list = new ArrayList<>();
    private final SensingRepository sensingRepository;
    private final UserSetRepository userSetRepository;
    private final FirebaseService firebaseService;
    private final UserDeviceRepository userDeviceRepository;
    private final DeviceIdRepository deviceIdRepository;
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {



        String payload = message.getPayload();
        log.info("payload : " + payload);
        JSONObject jsonObject = new JSONObject(payload);
        Double temperature = jsonObject.getDouble("temperature");
        Integer waterLevel = jsonObject.getInt("waterLevel");
        Double turbidity = jsonObject.getDouble("turbidity");
        Double ph = jsonObject.getDouble("ph");
        Long deviceId = jsonObject.getLong("deviceId");
        String checkLeftovers = jsonObject.getString("checkLeftovers");
        getSensing(temperature,waterLevel,turbidity,ph,deviceId,checkLeftovers);

        Optional<DeviceId> byId = deviceIdRepository.findById(deviceId);
        Optional<UserSet> byDeviceId = userSetRepository.findByDeviceId(byId.get());
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("temperature", byDeviceId.get().getUserSetTemperature().toString());
        stringStringHashMap.put("waterLevel",byDeviceId.get().getUserSetWaterLevel().toString());
        stringStringHashMap.put("turbidity",byDeviceId.get().getUserSetTurbidity().toString());
        stringStringHashMap.put("ph",byDeviceId.get().getUserSetPh().toString());
        session.sendMessage(new TextMessage(stringStringHashMap.toString()));
    }


    /* Client가 접속 시 호출되는 메서드 */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        list.add(session);
        log.info(session + " 클라이언트 접속");
    }

    /* Client가 접속 해제 시 호출되는 메서드드 */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info(session + " 클라이언트 접속 해제");
        list.remove(session);
    }

    public String getSensing(Double temperature, Integer waterLevel, Double turbidity, Double ph, Long deviceId, String checkLeftovers) throws FirebaseMessagingException {
        DeviceId sensingDeviceId = DeviceId.builder().id(deviceId).build();
        log.info("온도 : {}",temperature);
        log.info("물 수위 : {}",waterLevel);
        log.info("탁도 : {}",turbidity);
        log.info("ph : {}",ph);
        log.info("deviceId :  {}",deviceId);
        Optional<DeviceId> byId = deviceIdRepository.findById(deviceId);
        Sensing sensing = Sensing.builder()
                .measuredTemperature(temperature)
                .measuredWaterLevel(waterLevel)
                .measuredTurbidity(turbidity)
                .measuredPh(ph)
                .deviceId(byId.get()).build();

        if(checkLeftovers.isEmpty()){
            if(!sensingRepository.findByDeviceId(byId.get()).isEmpty()){
                Optional<Sensing> byDeviceId = sensingRepository.findByDeviceId(byId.get());
                byDeviceId.get().setMeasuredTemperature(temperature);
                byDeviceId.get().setMeasuredWaterLevel(waterLevel);
                byDeviceId.get().setMeasuredPh(ph);
                byDeviceId.get().setMeasuredTurbidity(turbidity);
                sensingRepository.save(byDeviceId.get());
            }
            else {
                sensingRepository.save(sensing);
            }
        }

        Optional<UserDevice> tokenByDeviceId = userDeviceRepository.findByDeviceId(sensingDeviceId);
        String firebaseToken = tokenByDeviceId.get().getUserId().getFireBaseToken();
        //푸쉬 알림 보내기
        if (isSensingTemperatureLow(temperature,deviceId)){
            firebaseService.sendTemperatureLowMessage(firebaseToken,temperature,deviceId);
        }
        if (isSensingTemperatureHigh(temperature,deviceId)){
            firebaseService.sendTemperatureHighMessage(firebaseToken,temperature,deviceId);
        }
        if (isSensingWaterLevelLow(waterLevel,deviceId)){
            firebaseService.sendWaterLevelLowMessage(firebaseToken,waterLevel,deviceId);
        }
        if (isSensingPhLow(ph,deviceId)){
            firebaseService.sendPhLowMessage(firebaseToken,ph,deviceId);
        }
        if (isSensingPhHigh(ph,deviceId)){
            firebaseService.sendPhHighessage(firebaseToken,ph,deviceId);
        }
        if (isSensingTurbidity(turbidity, deviceId)){
            firebaseService.sendTurbidityMessage(firebaseToken,turbidity,deviceId);
        }
        if (isSensingLeftovers(checkLeftovers,deviceId)){
            firebaseService.sendLeftoversMessage(firebaseToken,deviceId);
        }
        return "OK";
    }

    //푸쉬 알림 조건
    public Boolean isSensingTemperatureLow(Double temperature, Long deviceId){
        DeviceId build = DeviceId.builder().id(deviceId).build();
        Optional<UserSet> byDeviceId = userSetRepository.findByDeviceId(build);
        if(byDeviceId.get().getUserSetTemperature() > temperature){
            return true;
        }
        else
            return false;
    }

    public Boolean isSensingTemperatureHigh(Double temperature, Long deviceId){
        DeviceId build = DeviceId.builder().id(deviceId).build();
        Optional<UserSet> byDeviceId = userSetRepository.findByDeviceId(build);
        if(byDeviceId.get().getUserSetTemperature() < temperature){
            return true;
        }
        else
            return false;
    }

    public Boolean isSensingWaterLevelLow(Integer waterLevel, Long deviceId){
        DeviceId build = DeviceId.builder().id(deviceId).build();
        Optional<UserSet> byDeviceId = userSetRepository.findByDeviceId(build);
        if(byDeviceId.get().getUserSetWaterLevel() > waterLevel){
            return true;
        }
        else
            return false;
    }

    public Boolean isSensingPhLow(Double ph, Long deviceId){
        DeviceId build = DeviceId.builder().id(deviceId).build();
        Optional<UserSet> byDeviceId = userSetRepository.findByDeviceId(build);
        if(byDeviceId.get().getUserSetPh() > ph ){
            return true;
        }
        else
            return false;
    }

    public Boolean isSensingPhHigh(Double ph, Long deviceId){
        DeviceId build = DeviceId.builder().id(deviceId).build();
        Optional<UserSet> byDeviceId = userSetRepository.findByDeviceId(build);
        if(byDeviceId.get().getUserSetPh() < ph ){
            return true;
        }
        else
            return false;
    }

    public Boolean isSensingTurbidity(Double turbidity, Long deviceId){
        DeviceId build = DeviceId.builder().id(deviceId).build();
        Optional<UserSet> byDeviceId = userSetRepository.findByDeviceId(build);
        if (byDeviceId.get().getUserSetTurbidity() != turbidity){
            return true;
        }
        else
            return false;
    }

    public Boolean isSensingLeftovers(String checkLeftovers, Long deviceId){
        DeviceId build = DeviceId.builder().id(deviceId).build();
        if (checkLeftovers.isEmpty()){
            return false;
        }
        else
            return true;
    }
}