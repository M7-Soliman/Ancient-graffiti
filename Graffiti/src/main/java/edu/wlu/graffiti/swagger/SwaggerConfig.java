package edu.wlu.graffiti.swagger;

// Commenting out until have a chance to fix.


/**
 * 
 * @author Hammad Ahmad
 * @author Trevor Stalnaker
 *
 */
/*
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import static springfox.documentation.builders.PathSelectors.regex;
*/
//@Configuration
//@EnableSwagger2
public class SwaggerConfig {
	/*
	
	@Bean
	public Docket searchApi() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage("edu.wlu.graffiti.controller"))
				.paths(regex(".*csv|.*json|.*xml|/filter.*|/results.*|/graffito.*"))
				.build()
				.apiInfo(metaData())
				.tags(new Tag("graffiti-controller", "Operations pertaining to the graffiti."),
						new Tag("csv-controller", "Operations pertaining to CSV data exports."),
						new Tag("epidoc-controller", "Operations pertaining to EpiDoc exports."),
						new Tag("json-controller", "Operations pertaining to JSON data exports."));
	}
	
	private ApiInfo metaData() {
		ApiInfo apiInfo = new ApiInfoBuilder()
			   .title("AGP Search API")
			   .description("REST API for Ancient Graffiti Project")
			   .termsOfServiceUrl("http://ancientgraffiti.org/about/main/terms-of-use/")
			   .version("4.0")
			   .contact(contact())
			   .license("Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License")
			   .licenseUrl("https://creativecommons.org/licenses/by-nc-sa/4.0/")
			   .build();
		return apiInfo;
	}
	
	private Contact contact() {
		Contact c = new Contact(null, null, null);
		return c;
	}
	*/
}
