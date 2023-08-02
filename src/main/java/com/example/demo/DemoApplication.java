package com.example.demo;

import org.im4java.core.GraphicsMagickCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;

@SpringBootApplication
@RestController
public class DemoApplication {
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@GetMapping("users")
	public String hello() throws Exception {

		GraphicsMagickCmd cmd = new GraphicsMagickCmd("convert");

		// create the operation, add images and operators/options
		IMOperation op = new IMOperation();

		// resize an image
		op.addImage("/Users/kyle/Internship/scratch/Sunflower_from_Silesia2.jpeg");
		op.resize(800,600);
		op.addImage("/Users/kyle/Internship/scratch/ilovejava.jpeg");

//		execute the operation
		cmd.run(op);

		return "Hello world!";
	}
}
