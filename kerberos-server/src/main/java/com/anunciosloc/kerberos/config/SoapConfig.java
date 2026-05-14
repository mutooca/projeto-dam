package com.anunciosloc.kerberos.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

@EnableWs
@Configuration
public class SoapConfig {

    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(
            ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean<>(servlet, "/ws/*");
    }

    @Bean(name = "kerberos")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema kerberosSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("KerberosPort");
        wsdl11Definition.setLocationUri("/ws/kerberos");
        wsdl11Definition.setTargetNamespace("http://kerberos.anunciosloc.com/");
        wsdl11Definition.setSchema(kerberosSchema);
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema kerberosSchema() {
        return new SimpleXsdSchema(new ClassPathResource("kerberos.xsd"));
    }
}