package com.example.shopfinder;

import com.example.shopfinder.shop.Address;
import com.example.shopfinder.shop.Shop;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static org.assertj.core.api.BDDAssertions.then;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ShopFinderApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ShopFinderApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate testRestTemplate;

    @Before
    public void init(){
        this.testRestTemplate.delete("http://localhost:" + this.port + "/shops");
    }

	@Test
	public void shouldReturn204WhenSendingRequestToControllerWithNoShops() throws Exception {
		@SuppressWarnings("rawtypes")
		ResponseEntity entity = this.testRestTemplate.getForEntity(
				"http://localhost:" + this.port + "/shops", Object.class);

		then(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}

    @Test
    public void shouldReturn200WhenPOSTingAnAlreadyPresentShopToController() throws Exception {
        Shop zooShop1 = new Shop("London Zoo", new Address("1", "NW1 4RY"));
        @SuppressWarnings("rawtypes")
        ResponseEntity entity1 = this.testRestTemplate.postForEntity(
                "http://localhost:" + this.port + "/shops", zooShop1, Object.class);

        then(entity1.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    public void shouldReturn400WhenPOSTingAnAlreadyPresentShopToController() throws Exception {
        Shop zooShop1 = new Shop("London Zoo", new Address("1", "NW1 4RY"));
        Shop zooShop2 = new Shop("London Zoo", new Address("1", "NW14RY"));

        @SuppressWarnings("rawtypes")
        ResponseEntity entity1 = this.testRestTemplate.postForEntity(
                "http://localhost:" + this.port + "/shops", zooShop1, Object.class);

        then(entity1.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        @SuppressWarnings("rawtypes")
        ResponseEntity entity2 = this.testRestTemplate.postForEntity(
                "http://localhost:" + this.port + "/shops", zooShop2, Object.class);

        then(entity2.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }


}