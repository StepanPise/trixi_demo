package com.trixi.pise_trixi;

import com.trixi.pise_trixi.Service.DataImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class PiseTrixiApplication implements CommandLineRunner {

	private final DataImportService dataImportService;

	public static void main(String[] args) {
		SpringApplication.run(PiseTrixiApplication.class, args);
	}

	@Override
	public void run(String... args) {
		dataImportService.importData();
	}
}