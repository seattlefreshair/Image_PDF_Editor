package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import  springfox.documentation.builders.ApiInfoBuilder ;
import  springfox.documentation.builders.PathSelectors ;
import  springfox.documentation.builders.RequestHandlerSelectors ;
import springfox.documentation.service.Contact;
import  springfox.documentation.spi.DocumentationType ;
import  springfox.documentation.spring.web.plugins.Docket ;
import  springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc ;

@Configuration
@EnableSwagger2WebMvc
public class Knife4jConfiguration {

    @Bean(value = "defaultApi2")
    public Docket defaultApi2() {
        Docket  docket = new  Docket ( DocumentationType . SWAGGER_2 )
                .apiInfo ( new ApiInfoBuilder ( )
                        //.title("swagger-bootstrap-ui-demo RESTful APIs")
                        .description ( "# swagger-bootstrap-ui - demo RESTful APIs" )
                        .termsOfServiceUrl ( "http://www.xx.com/")
                        .contact ( new Contact("kz", "http://www.xx.com/", "xx@qq.com"))
                        .version ( " 1.0" )
                        .build ( ))
                //Group Name
                .groupName ( " Version 2.X" )
                .select ( )
                //Specify the Controller scan package path here
                .apis ( RequestHandlerSelectors . basePackage ( "com.example.demo.controller" ) )
                .paths ( PathSelectors . any () )
                .build () ;
        return  docket;
    }
}