package kr.hhplus.be.server;

import jakarta.annotation.PreDestroy;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

//@Configuration
class TestcontainersConfiguration {

	public static final MySQLContainer<?> MYSQL_CONTAINER;

	static {
		MYSQL_CONTAINER = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
			.withDatabaseName("concert")
			.withUsername("root")
			.withPassword("1234");
		MYSQL_CONTAINER.start();
	}

	@PreDestroy
	public void preDestroy() {
		if (MYSQL_CONTAINER.isRunning()) {
			MYSQL_CONTAINER.stop();
		}
	}
}