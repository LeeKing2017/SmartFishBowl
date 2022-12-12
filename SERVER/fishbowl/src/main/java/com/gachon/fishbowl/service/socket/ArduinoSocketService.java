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
        try {
            getSensing(temperature,waterLevel,turbidity,ph,deviceId,checkLeftovers);
        }
        catch (FirebaseMessagingException e){
            log.info("firebase error");
        }

        JSONObject returnJsonObject = new JSONObject();
        Optional<DeviceId> byId = deviceIdRepository.findById(deviceId);
        Optional<UserSet> byDeviceId = userSetRepository.findByDeviceId(byId.get());
        if(byDeviceId.isPresent()) {
            Optional<UserSetFoodTime> byUserSet = userSetFoodTimeRepository.findByUserSet(byDeviceId.get());
//            HashMap<String, String> stringStringHashMap = new HashMap<>();
            //temperature 보내기
            if(byDeviceId.get().getUserSetTemperature() != null) {
//                stringStringHashMap.put("\"temperature\"", byDeviceId.get().getUserSetTemperature().toString());
                returnJsonObject.put("temperature",byDeviceId.get().getUserSetTemperature().toString());
            }
            else {
//                stringStringHashMap.put("\"temperature\"", "\"\"");
                returnJsonObject.put("temperature","");
            }
            //첫번째 먹이 시간/횟수 보내기
            if (byUserSet.isPresent()) {
                if (byUserSet.get().getNumberOfFirstFeedings() != null) {
                    /*stringStringHashMap.put("\"firstTime\"", byUserSet.get().getFirstTime().toString());
                    stringStringHashMap.put("\"numberOfFirstFeedings\"", byUserSet.get().getNumberOfFirstFeedings().toString());*/
                    returnJsonObject.put("firstTime",byUserSet.get().getFirstTime().toString()+":00");
                    returnJsonObject.put("numberOfFirstFeedings",byUserSet.get().getNumberOfFirstFeedings().toString());
                } else {
                   /* stringStringHashMap.put("\"firstTime\"", "\"\"");
                    stringStringHashMap.put("\"numberOfFirstFeedings\"", "\"\"");*/
                    returnJsonObject.put("firstTime","");
                    returnJsonObject.put("numberOfFirstFeedings","");
                }
                //두번째 먹이 시간/횟수 보내기
                if (byUserSet.get().getNumberOfSecondFeedings() != null) {
                    /*stringStringHashMap.put("\"secondTime\"", byUserSet.get().getSecondTime().toString());
                    stringStringHashMap.put("\"numberOfSecondFeedings\"", byUserSet.get().getNumberOfSecondFeedings().toString());*/
                    returnJsonObject.put("secondTime",byUserSet.get().getSecondTime().toString()+":00");
                    returnJsonObject.put("numberOfSecondFeedings",byUserSet.get().getNumberOfSecondFeedings().toString());
                } else {
                  /*  stringStringHashMap.put("\"secondTime\"", "\"\"");
                    stringStringHashMap.put("\"numberOfSecondFeedings\"", "\"\"");*/
                    returnJsonObject.put("secondTime","");
                    returnJsonObject.put("numberOfSecondFeedings","");
                }
                //세번째 먹이 시간/횟수 보내기
                if (byUserSet.get().getNumberOfThirdFeedings() != null) {
                    /*stringStringHashMap.put("\"thirdTime\"", byUserSet.get().getThirdTime().toString());
                    stringStringHashMap.put("\"numberOfThirdFeedings\"", byUserSet.get().getNumberOfThirdFeedings().toString());*/
                    returnJsonObject.put("thirdTime",byUserSet.get().getThirdTime().toString()+":00");
                    returnJsonObject.put("numberOfThirdFeedings",byUserSet.get().getNumberOfThirdFeedings().toString());
                } else {
                    /*stringStringHashMap.put("\"thirdTime\"", "\"\"");
                    stringStringHashMap.put("\"numberOfThirdFeedings\"", "\"\"");*/
                    returnJsonObject.put("thirdTime","");
                    returnJsonObject.put("numberOfThirdFeedings","");
                }
            }
            else{
               /* stringStringHashMap.put("\"firstTime\"", "\"\"");
                stringStringHashMap.put("\"numberOfFirstFeedings\"", "\"\"");
                stringStringHashMap.put("\"secondTime\"", "\"\"");
                stringStringHashMap.put("\"numberOfSecondFeedings\"", "\"\"");
                stringStringHashMap.put("\"thirdTime\"", "\"\"");
                stringStringHashMap.put("\"numberOfThirdFeedings\"", "\"\"");*/
                returnJsonObject.put("firstTime","");
                returnJsonObject.put("numberOfFirstFeedings","");
                returnJsonObject.put("secondTime","");
                returnJsonObject.put("numberOfSecondFeedings","");
                returnJsonObject.put("thirdTime","");
                returnJsonObject.put("numberOfThirdFeedings","");
            }
            //userSet 데이터 소켓으로 보내기
            session.sendMessage(new TextMessage(returnJsonObject.toString()));
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
        Optional<DeviceId> byId = deviceIdRepository.findById(deviceId);


        Sensing sensing = Sensing.builder()
                .measuredTemperature(temperature)
                .measuredWaterLevel(waterLevel)
                .measuredTurbidity(turbidity)
                .measuredPh(ph)
                .deviceId(byId.get()).build();

        if(checkLeftovers.isEmpty()){
            if(sensingRepository.findByDeviceId(byId.get()).isPresent()){
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

        Optional<UserSet> byDeviceId1 = userSetRepository.findByDeviceId(byId.get());
        if (byDeviceId1.isPresent()) {
            Double userSetTemperature = byDeviceId1.get().getUserSetTemperature();
            Integer userSetWaterLevel = byDeviceId1.get().getUserSetWaterLevel();
            Double userSetTurbidity = byDeviceId1.get().getUserSetTurbidity();
            Double userSetPh = byDeviceId1.get().getUserSetPh();

            Optional<UserDevice> tokenByDeviceId = userDeviceRepository.findByDeviceId(sensingDeviceId);
            String firebaseToken = tokenByDeviceId.get().getUserId().getFireBaseToken();
            //푸쉬 알림 보내기

            if (userSetTemperature != null){
                if (isSensingTemperatureLow(temperature, userSetTemperature, deviceId)) {
                    firebaseService.sendTemperatureLowMessage(firebaseToken, temperature, deviceId);
                    log.info("온도가 낮음");
                }
                if (isSensingTemperatureHigh(temperature, userSetTemperature, deviceId)) {
                    firebaseService.sendTemperatureHighMessage(firebaseToken, temperature, deviceId);
                    log.info("온도가 높음");
                }
            }
            if (userSetWaterLevel != null) {
                if (isSensingWaterLevelLow(waterLevel, userSetWaterLevel, deviceId)) {
                    firebaseService.sendWaterLevelLowMessage(firebaseToken, waterLevel, deviceId);
                    log.info("물 수위가 낮아짐");
                }
            }
            if (userSetPh != null) {
                if (isSensingPhLow(ph, userSetPh, deviceId)) {
                    firebaseService.sendPhLowMessage(firebaseToken, ph, deviceId);
                    log.info("ph가 낮아짐");
                }
                if (isSensingPhHigh(ph, userSetPh, deviceId)) {
                    firebaseService.sendPhHighMessage(firebaseToken, ph, deviceId);
                    log.info("ph가 높아짐");
                }
            }
            if (userSetTurbidity != null) {
                if (isSensingTurbidity(turbidity, userSetTurbidity, deviceId)) {
                    firebaseService.sendTurbidityMessage(firebaseToken, turbidity, deviceId);
                    log.info("탁도가 높음");
                }
            }

            if (isSensingLeftovers(checkLeftovers)) {
                firebaseService.sendLeftoversMessage(firebaseToken, deviceId);
                log.info("먹이 부족");
            }
        }
        return "OK";

    }

    //푸쉬 알림 조건
    public Boolean isSensingTemperatureLow(Double temperature, Double dbTemperature, Long deviceId){
        DeviceId build = DeviceId.builder().id(deviceId).build();
        Optional<UserSet> byDeviceId = userSetRepository.findByDeviceId(build);
        dbTemperature = (double) (Math.round(dbTemperature* 100)/10)*0.1;
        Double dtoTemperature = (double) (Math.round(temperature* 100)/10)*0.1;
        if(byDeviceId.isPresent() && byDeviceId.get().getUserSetTemperature() != null) {
            if (dbTemperature.compareTo(dtoTemperature) == 0) {
                log.info("DB 센싱 : {}", dbTemperature);
                log.info("DTO 센싱 : {}", dtoTemperature);
                log.info("== 결과 : {}", (dbTemperature.compareTo(dtoTemperature) == 0));
                log.info("센싱과 설정 온도가 같음 낮");
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
        dbTemperature = (double) (Math.round(dbTemperature* 100)/10)*0.1;
        Double dtoTemperature = (double) (Math.round(temperature* 100)/10)*0.1;
        if(byDeviceId.isPresent() && byDeviceId.get().getUserSetTemperature() != null) {
            if (dbTemperature.compareTo(dtoTemperature) == 0) {
                log.info("DB 센싱 : {}", dbTemperature);
                log.info("DTO 센싱 : {}", dtoTemperature);
                log.info("== 결과 : {}", (dbTemperature.compareTo(dtoTemperature) == 0));
                log.info("센싱과 설정 온도가 같음 높");
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
        dbWaterLevel = dbWaterLevel;
        Integer dtoWaterLevel = waterLevel   ;
        if(byDeviceId.isPresent() && byDeviceId.get().getUserSetWaterLevel() != null) {
            if (dbWaterLevel.equals(dtoWaterLevel)) {
                log.info("DB 센싱 : {}", dbWaterLevel);
                log.info("DTO 센싱 : {}", dtoWaterLevel);
                log.info("== 결과 : {}", (dbWaterLevel.compareTo(dtoWaterLevel) == 0));
                log.info("센싱과 설정 수위가 같음");
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
        dbPh = (double) (Math.round(dbPh * 100)/10)*0.1;
        Double dtoPh = (double) (Math.round(ph * 100)/10)*0.1;
        if(byDeviceId.isPresent() && byDeviceId.get().getUserSetPh() != null) {
            if (dbPh.compareTo(dtoPh) == 0) {
                log.info("DB 센싱 : {}", dbPh);
                log.info("DTO 센싱 : {}", dtoPh);
                log.info("== 결과 : {}", (dbPh.compareTo(dtoPh) == 0));
                log.info("센싱과 설정 ph가 같음 낮");
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
        dbPh = (double) (Math.round(dbPh * 100)/10)*0.1;
        Double dtoPh = (double) (Math.round(ph * 100)/10)*0.1;
        if (byDeviceId.isPresent() && byDeviceId.get().getUserSetPh() != null){
            if (dbPh.compareTo(dtoPh) == 0){
                log.info("DB 센싱 : {}", dbPh);
                log.info("DTO 센싱 : {}", dtoPh);
                log.info("== 결과 : {}", (dbPh.compareTo(dtoPh) == 0));
                log.info("센싱과 설정 ph가 같음 높");
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
        dbTurbidity = (double) (Math.round(dbTurbidity * 100)/10)*0.1;
        Double dtoTurbidity = (double) (Math.round(turbidity * 100)/10)*0.1;
        if (byDeviceId.isPresent() && byDeviceId.get().getUserSetTurbidity() != null){
            if (dbTurbidity.compareTo(dtoTurbidity) == 0){
                log.info("DB 센싱 : {}", dbTurbidity);
                log.info("DTO 센싱 : {}", dtoTurbidity);
                log.info("== 결과 : {}", (dbTurbidity.compareTo(dtoTurbidity) == 0));
                log.info("센싱과 설정 탁도가 같음");
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