/*
 * Copyright 2012-present the original author or authors.
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

package org.springframework.boot.zipkin.autoconfigure;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import zipkin2.reporter.BaseHttpSender;
import zipkin2.reporter.BytesMessageSender;
import zipkin2.reporter.Encoding;
import zipkin2.reporter.HttpEndpointSupplier.Factory;

import org.springframework.util.unit.DataSize;

/**
 * A Zipkin {@link BytesMessageSender} that uses an HTTP client to send JSON spans.
 * Supports automatic compression with gzip.
 *
 * @author Moritz Halbritter
 * @author Stefan Bratanov
 */
abstract class HttpSender extends BaseHttpSender<URI, byte[]> {

	/**
	 * Only use gzip compression on data which is bigger than this in bytes.
	 */
	private static final DataSize COMPRESSION_THRESHOLD = DataSize.ofKilobytes(1);

	HttpSender(Encoding encoding, Factory endpointSupplierFactory, String endpoint) {
		super(encoding, endpointSupplierFactory, endpoint);
	}

	@Override
	protected URI newEndpoint(String endpoint) {
		return URI.create(endpoint);
	}

	@Override
	protected byte[] newBody(List<byte[]> list) {
		return this.encoding.encode(list);
	}

	@Override
	protected void postSpans(URI endpoint, byte[] body) throws IOException {
		Map<String, String> headers = getDefaultHeaders();
		if (needsCompression(body)) {
			body = compress(body);
			headers.put("Content-Encoding", "gzip");
		}
		postSpans(endpoint, headers, body);
	}

	abstract void postSpans(URI endpoint, Map<String, String> headers, byte[] body) throws IOException;

	Map<String, String> getDefaultHeaders() {
		Map<String, String> headers = new LinkedHashMap<>();
		headers.put("b3", "0");
		headers.put("Content-Type", this.encoding.mediaType());
		return headers;
	}

	private boolean needsCompression(byte[] body) {
		return body.length > COMPRESSION_THRESHOLD.toBytes();
	}

	private byte[] compress(byte[] input) throws IOException {
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		try (GZIPOutputStream gzip = new GZIPOutputStream(result)) {
			gzip.write(input);
		}
		return result.toByteArray();
	}

}
