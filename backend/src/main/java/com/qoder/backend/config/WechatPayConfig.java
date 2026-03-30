package com.qoder.backend.config;

import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.notification.NotificationConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "wechat.pay")
@ConditionalOnProperty(prefix = "wechat.pay", name = "enabled", havingValue = "true")
public class WechatPayConfig {
    
    /**
     * 商户号
     */
    private String mchId;
    
    /**
     * 公众号/小程序AppID
     */
    private String appId;
    
    /**
     * APIv3密钥
     */
    private String apiV3Key;
    
    /**
     * 商户API证书序列号
     */
    private String certSerialNo;
    
    /**
     * 支付回调通知地址
     */
    private String notifyUrl;
    
    /**
     * 商户API私钥
     */
    private String privateKey;
    
    /**
     * 微信支付配置
     */
    @Bean
    public Config wechatPayCoreConfig() {
        return new RSAAutoCertificateConfig.Builder()
                .merchantId(mchId)
                .privateKey(privateKey)
                .merchantSerialNumber(certSerialNo)
                .apiV3Key(apiV3Key)
                .build();
    }
    
    /**
     * Native支付服务
     */
    @Bean
    public NativePayService nativePayService(Config config) {
        return new NativePayService.Builder().config(config).build();
    }
    
    /**
     * 通知解析器
     */
    @Bean
    public NotificationParser notificationParser(Config config) {
        return new NotificationParser((NotificationConfig) config);
    }
}
