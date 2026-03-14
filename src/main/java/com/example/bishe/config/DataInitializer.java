package com.example.bishe.config;

import com.example.bishe.entity.*;
import com.example.bishe.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Random;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private EnvironmentDataRepository environmentDataRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private VideoMonitorRepository videoMonitorRepository;

    @Autowired
    private UserRepository userRepository;

    private final Random random = new Random();

    @Override
    public void run(String... args) {
        initUsers();
        initDevices();
        initEnvironmentData();
        initAlerts();
        initVideoMonitors();
    }

    private void initUsers() {
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword("admin123");
        admin.setNickname("管理员");
        admin.setEmail("admin@example.com");
        admin.setPhone("13800138000");
        admin.setAge(30);
        admin.setGender("男");
        admin.setRole("admin");
        userRepository.save(admin);

        User user = new User();
        user.setUsername("user");
        user.setPassword("user123");
        user.setNickname("普通用户");
        user.setEmail("user@example.com");
        user.setPhone("13800138001");
        user.setAge(25);
        user.setGender("女");
        user.setRole("user");
        userRepository.save(user);
    }

    private void initDevices() {

        String[] deviceTypes = {"温度传感器", "湿度传感器", "CO2传感器", "氨气传感器", "光照传感器", "摄像头", "风机", "加湿器"};
        String[] locations = {"1号养殖区", "2号养殖区", "3号养殖区", "4号养殖区", "中央控制室"};
        String[] statuses = {"在线", "离线", "维护中"};

        for (int i = 1; i <= 15; i++) {
            Device device = new Device();
            device.setDeviceId("DEV" + String.format("%03d", i));
            device.setName(deviceTypes[i % deviceTypes.length] + "-" + i);
            device.setDeviceType(deviceTypes[i % deviceTypes.length]);
            device.setLocation(locations[i % locations.length]);
            device.setStatus(statuses[i % statuses.length]);
            device.setControlParams("{\"threshold\": " + (20 + random.nextInt(10)) + "}");
            deviceRepository.save(device);
        }
    }

    private void initEnvironmentData() {

        // 生成最近24小时的环境数据，每30分钟一条
        LocalDateTime now = LocalDateTime.now();
        for (int i = 48; i >= 0; i--) {
            EnvironmentData data = new EnvironmentData();
            data.setDeviceId("DEV001");
            data.setTimestamp(now.minusMinutes(i * 30));

            // 模拟温度和湿度的波动
            double baseTemp = 25.0;
            double baseHumidity = 60.0;
            double tempVariation = Math.sin(i * 0.2) * 5 + random.nextDouble() * 2 - 1;
            double humidityVariation = Math.cos(i * 0.15) * 10 + random.nextDouble() * 5 - 2.5;

            data.setTemperature(baseTemp + tempVariation);
            data.setHumidity(baseHumidity + humidityVariation);
            data.setCo2(400.0 + random.nextInt(200));
            data.setAmmonia(random.nextDouble() * 5);
            data.setLightIntensity(500.0 + random.nextInt(500));

            environmentDataRepository.save(data);
        }
    }

    private void initAlerts() {

        String[] alertTypes = {"温度异常", "湿度过高", "CO2超标", "设备离线", "氨气泄漏"};
        String[] alertLevels = {"一般", "严重", "紧急"};

        LocalDateTime now = LocalDateTime.now();

        for (int i = 1; i <= 20; i++) {
            Alert alert = new Alert();
            alert.setType(alertTypes[i % alertTypes.length]);
            alert.setLevel(alertLevels[i % alertLevels.length]);
            alert.setMessage(alertTypes[i % alertTypes.length] + "，请立即检查！");
            alert.setTimestamp(now.minusHours(random.nextInt(48)));
            alert.setStatus(i % 3 == 0 ? "已处理" : "未处理");
            alert.setDeviceId("DEV" + String.format("%03d", random.nextInt(15) + 1));
            alertRepository.save(alert);
        }
    }

    private void initVideoMonitors() {

        String[] locations = {"1号养殖区入口", "2号养殖区入口", "3号养殖区入口", "饲料仓库", "中央控制室", "出入口", "围栏周边"};
        String[] statuses = {"在线", "离线"};

        for (int i = 1; i <= 8; i++) {
            VideoMonitor monitor = new VideoMonitor();
            monitor.setName("摄像头-" + i);
            monitor.setDeviceId("CAM" + String.format("%03d", i));
            monitor.setLocation(locations[i % locations.length]);
            monitor.setStatus(statuses[i % statuses.length]);
            videoMonitorRepository.save(monitor);
        }
    }
}
