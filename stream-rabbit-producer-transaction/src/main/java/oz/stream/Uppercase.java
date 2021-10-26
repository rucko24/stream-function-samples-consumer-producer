/*
 * Copyright 2021-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package oz.stream;

import java.util.function.Consumer;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Oleg Zhurakousky
 *
 */
@Component
public class Uppercase implements Consumer<String> {

	private final StreamBridge streamBridge;

	public Uppercase(StreamBridge streamBridge) {
		this.streamBridge = streamBridge;
	}

	@Override
	@Transactional
	public void accept(String input) {
		System.out.println("Uppercasing " + input);
		this.streamBridge.send("uppercase-out", input.toUpperCase());
		if (input.equals("fail")) {
			System.out.println("throwing exception");
			throw new RuntimeException("Itentional");
		}
	}

}
