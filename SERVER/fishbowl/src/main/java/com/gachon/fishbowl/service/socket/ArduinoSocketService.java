package com.gachon.fishbowl.service.socket;

import com.gachon.fishbowl.entity.*;
import com.gachon.fishbowl.repository.*;
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
    private final UserSetFoodTimeRepository userSetFoodTimeRepository;
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
        if(byDeviceId.isPresent()) {
            Optional<UserSetFoodTime> byUserSet = userSetFoodTimeRepository.findByUserSet(byDeviceId.get());
            HashMap<String, String> stringStringHashMap = new HashMap<>();
            if(byDeviceId.get().getUserSetTemperature() != null) {
                stringStringHashMap.put("temperature", byDeviceId.get().getUserSetTemperature().toString());
            }
            if(byUserSet.get().getNumberOfFirstFeedings() != null) {
                stringStringHashMap.put("firstTime", byUserSet.get().getFirstTime().toString());
                stringStringHashMap.put("numberOfFirstFeedings", byUserSet.get().getNumberOfFirstFeedings().toString());
            }
            if (byUserSet.get().getNumberOfSecondFeedings() != null) {
                stringStringHashMap.put("secondTime", byUserSet.get().getSecondTime().toString());
                stringStringHashMap.put("numberOfSecondFeedings", byUserSet.get().getNumberOfSecondFeedings().toString());
            }
            if (byUserSet.get().getNumberOfThirdFeedings() != null) {
                stringStringHashMap.put("thirdTime", byUserSet.get().getThirdTime().toString());
                stringStringHashMap.put("numberOfThirdFeedings", byUserSet.get().getNumberOfThirdFeedings().toString());
            }
            if (!stringStringHashMap.isEmpty()) {
                session.sendMessage(new TextMessage(stringStringHashMap.toString()));
            }
        }
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
        Double dbTemperature = 0.0;
        Integer dbWaterLevel = 0;
        Double dbTurbidity = 0.0;
        Double dbPh = 0.0;
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
                dbTemperature = byDeviceId.get().getMeasuredTemperature();
                dbWaterLevel = byDeviceId.get().getMeasuredWaterLevel();
                dbTurbidity = byDeviceId.get().getMeasuredTurbidity();
                dbPh = byDeviceId.get().getMeasuredPh();
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
        if (isSensingTemperatureLow(temperature,dbTemperature, deviceId)){
            firebaseService.sendTemperatureLowMessage(firebaseToken,temperature,deviceId);
        }
        if (isSensingTemperatureHigh(temperature,dbTemperature, deviceId)){
            firebaseService.sendTemperatureHighMessage(firebaseToken,temperature,deviceId);
        }
        if (isSensingWaterLevelLow(waterLevel,dbWaterLevel, deviceId)){
            firebaseService.sendWaterLevelLowMessage(firebaseToken,waterLevel,deviceId);
        }
        if (isSensingPhLow(ph,dbPh, deviceId)){
            firebaseService.sendPhLowMessage(firebaseToken,ph,deviceId);
        }
        if (isSensingPhHigh(ph,dbPh, deviceId)){
            firebaseService.sendPhHighMessage(firebaseToken,ph,deviceId);
        }
        if (isSensingTurbidity(turbidity, dbTurbidity, deviceId)){
            firebaseService.sendTurbidityMessage(firebaseToken,turbidity,deviceId);
        }
        if (isSensingLeftovers(checkLeftovers)){
            firebaseService.sendLeftoversMessage(firebaseToken,deviceId);
        }
        return "OK";
    }

    //푸쉬 알림 조건
    public Boolean isSensingTemperatureLow(Double temperature, Double dbTemperature, Long deviceId){
        DeviceId build = DeviceId.builder().id(deviceId).build();
        Optional<UserSet> byDeviceId = userSetRepository.findByDeviceId(build);
        dbTemperature = (double) Math.round(dbTemperature);
        Double dtoTemperature = (double) Math.round(temperature);
        if(byDeviceId.isPresent() && byDeviceId.get().getUserSetTemperature() != null) {
            if (dbTemperature.compareTo(dtoTemperature) == 0) {
                log.info("DB 센싱 : {}", dbTemperature);
                log.info("DTO 센싱 : {}", dtoTemperature);
                log.info("== 결과 : {}", (dbTemperature.compareTo(dtoTemperature) == 0));
                return false;
            }

            if (byDeviceId.get().getUserSetTemperature() > temperature) {
                return true;
            } else {
                return false;
            }
        }
        else
            return false;
    }

    public Boolean isSensingTemperatureHigh(Double temperature, Double dbTemperature, Long deviceId){
        DeviceId build = DeviceId.builder().id(deviceId).build();
        Optional<UserSet> byDeviceId = userSetRepository.findByDeviceId(build);
        dbTemperature = (double) Math.round(dbTemperature);
        Double dtoTemperature = (double) Math.round(temperature);
        if(byDeviceId.isPresent() && byDeviceId.get().getUserSetTemperature() != null) {
            if (dbTemperature.compareTo(dtoTemperature) == 0) {
                log.info("DB 센싱 : {}", dbTemperature);
                log.info("DTO 센싱 : {}", dtoTemperature);
                log.info("== 결과 : {}", (dbTemperature.compareTo(dtoTemperature) == 0));
                return false;
            }

            if (byDeviceId.get().getUserSetTemperature() < temperature) {
                return true;
            } else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    public Boolean isSensingWaterLevelLow(Integer waterLevel, Integer dbWaterLevel, Long deviceId){
        DeviceId build = DeviceId.builder().id(deviceId).build();
        Optional<UserSet> byDeviceId = userSetRepository.findByDeviceId(build);
        dbWaterLevel = dbWaterLevel / 100;
        Integer dtoWaterLevel = waterLevel / 100;
        if(byDeviceId.isPresent() && byDeviceId.get().getUserSetWaterLevel() != null) {
            if (dbWaterLevel.equals(dtoWaterLevel)) {
                return false;
            }

            if (byDeviceId.get().getUserSetWaterLevel() > waterLevel) {
                return true;
            }else {
                return false;
            }
        }
        else{
            return false;
        }
    }

    public Boolean isSensingPhLow(Double ph, Double dbPh, Long deviceId){
        DeviceId build = DeviceId.builder().id(deviceId).build();
        Optional<UserSet> byDeviceId = userSetRepository.findByDeviceId(build);
        dbPh = (double) (Math.round(dbPh * 10) / 10);
        Double dtoPh = (double) (Math.round(ph * 10) / 10);
        if(byDeviceId.isPresent() && byDeviceId.get().getUserSetPh() != null) {
            if (dbPh.compareTo(dtoPh) == 0) {
                return false;
            }
            if (byDeviceId.get().getUserSetPh() > ph) {
                return true;
            } else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    public Boolean isSensingPhHigh(Double ph, Double dbPh, Long deviceId){
        DeviceId build = DeviceId.builder().id(deviceId).build();
        Optional<UserSet> byDeviceId = userSetRepository.findByDeviceId(build);
        dbPh = (double) (Math.round(dbPh * 10) / 10);
        Double dtoPh = (double) (Math.round(ph * 10) / 10);
        if (byDeviceId.isPresent() && byDeviceId.get().getUserSetPh() != null){
            if (dbPh.compareTo(dtoPh) == 0){
                return false;
            }
            if(byDeviceId.get().getUserSetPh() < ph ){
                return true;
            }
            else
                return false;
        }
        else{
            return false;
        }
    }

    public Boolean isSensingTurbidity(Double turbidity, Double dbTurbidity, Long deviceId){
        DeviceId build = DeviceId.builder().id(deviceId).build();
        Optional<UserSet> byDeviceId = userSetRepository.findByDeviceId(build);
        dbTurbidity = (double) (Math.round(dbTurbidity * 10) / 10);
        Double dtoTurbidity = (double) (Math.round(turbidity * 10) / 10);
        if (byDeviceId.isPresent() && byDeviceId.get().getUserSetTurbidity() != null){
            if (dbTurbidity.compareTo(dtoTurbidity) == 0){
                return false;
            }
            if (byDeviceId.get().getUserSetTurbidity() < turbidity){
                return true;
            }
            else
                return false;
        }
        else{
            return false;
        }
    }

    public Boolean isSensingLeftovers(String checkLeftovers){
        if (checkLeftovers.isEmpty()){
            return false;
        }
        else
            return true;
    }
}