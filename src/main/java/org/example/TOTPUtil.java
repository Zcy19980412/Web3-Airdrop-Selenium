package org.example;
import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.time.TimeProvider;
import dev.samstevens.totp.time.SystemTimeProvider;

public class TOTPUtil
{

    // 可选：自定义时间提供者，使用 System.currentTimeMillis()
    static class CustomTimeProvider implements TimeProvider {
        @Override
        public long getTime() {
            return System.currentTimeMillis();
        }
    }

    public static String generateTOTP(String secret) {
        try {
            TimeProvider timeProvider = new CustomTimeProvider();
            CodeGenerator codeGenerator = new DefaultCodeGenerator();
            long timeStepSeconds = 30;
            long currentTime = timeProvider.getTime() / 1000; // 当前 Unix 时间戳（秒）
            long timeStep = currentTime / timeStepSeconds;

            // 调试信息
            System.out.println("自定义时间戳 (秒): " + currentTime);
            System.out.println("时间步长: " + timeStep);

            String code = codeGenerator.generate(secret, timeStep);
            System.out.println("生成 TOTP 验证码 (自定义时间): " + code);
            return code;
        } catch (Exception e) {
            System.err.println("生成 TOTP 验证码失败 (自定义时间): " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}