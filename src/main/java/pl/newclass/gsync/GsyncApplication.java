package pl.newclass.gsync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GsyncApplication {

  public static void main(String[] args) {
    var applicationContext=SpringApplication.run(GsyncApplication.class, args);
  }

}
