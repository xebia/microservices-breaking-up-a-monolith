
import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class GetAnOrderShipped extends Simulation {

	val httpProtocol = http
		.baseURL("http://localhost:8080")
		.inferHtmlResources()

	val headers_0 = Map("Content-type" -> "application/json")

    val uri1 = "localhost"

  val random = new util.Random
  val feeder = Iterator.continually(Map("username" -> ("user" + random.nextString(20))))

	val scn = scenario("GetAnOrderShipped")
    .repeat(20) {
      feed(feeder)
      .exec(http("register user")
        .post("/monolith/user/register")
        .headers(headers_0)
        .body(StringBody("""{"username":"${username}","password":"pwdpwd"}"""))
        .basicAuth("user1","password")
        .check(
          jsonPath("$.uuid").saveAs("userUuid")
      ))
      .exec(
        http("creat cart")
        .post("/monolith/user/${userUuid}/cart")
        .headers(headers_0)
        .basicAuth("user1","password")
        .check(
          jsonPath("$.uuid").saveAs("cartUuid")
        )
      )
      // Create new product because of unique contraint issue in line item.
      .exec(
        http("create product")
        .post("/monolith/products")
        .headers(headers_0)
        .body(StringBody("""{"name":"fake product name","supplier": "supplier 1","price":12.45}"""))
        .basicAuth("user1","password")
        .check(
          jsonPath("$.uuid").saveAs("productUuid")
        )
      )
      .exec(
        http("add product to cart")
          .post("/monolith/cart/${cartUuid}/add")
          .headers(headers_0)
          .body(StringBody("""{"productId":"${productUuid}","quantity":20,"links":[]}"""))
          .basicAuth("user1","password")
      )
        .exec(
        http("create order")
          .post("/monolith/cart/${cartUuid}/order")
          .headers(headers_0)
          .basicAuth("user1","password")
          .check(
          jsonPath("$.uuid").saveAs("orderUuid")
        )
      )
        .exec(
        http("pay order")
          .put("/monolith/orders/${orderUuid}/pay")
          .headers(headers_0)
          .basicAuth("user1","password")
      )
      .exec(
        http("add account")
          .put("/monolith/orders/${orderUuid}/account")
          .headers(headers_0)
          .body(StringBody("""{"address":"fauxAddress","phoneNumber":"+31355381921","email":"info@xebia.com"}"""))
          .basicAuth("user1","password")
      )
      .exec(
        http("approve order")
          .put("/monolith/orders/${orderUuid}/approve")
          .headers(headers_0)
          .basicAuth("user1","password")
      )
      .exec(
        http("retrieve shipment id")
          .get("/monolith/shipment/getByOrder/${orderUuid}")
          .headers(headers_0)
          .basicAuth("user1","password")
          .check(
          jsonPath("$.uuid").saveAs("shipmentUuid")
        )
      )
      .exec(
        http("ship it")
          .put("/monolith/shipment/shipIt/${shipmentUuid}")
          .headers(headers_0)
          .basicAuth("user1","password")
      )
  }

  setUp(scn.inject(atOnceUsers(200))).protocols(httpProtocol)
}