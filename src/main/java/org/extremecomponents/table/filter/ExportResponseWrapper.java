/*
 * Copyright 2004 original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.extremecomponents.table.filter;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * @author Jeff Johnston
 */
public final class ExportResponseWrapper extends HttpServletResponseWrapper {
    private ByteArrayOutputStream output;
    private int contentLength;
    private String contentType;

    public ExportResponseWrapper(HttpServletResponse response) {
        super(response);
        output = new ByteArrayOutputStream();
    }

    public byte[] getData() {
        return output.toByteArray();
    }

    @Override
    public ServletOutputStream getOutputStream() {
        return new FilterServletOutputStream(output);
    }

    @Override
    public PrintWriter getWriter() {
        return new PrintWriter(getOutputStream(), true);
    }

    @Override
    public void setContentLength(int length) {
        this.contentLength = length;
        super.setContentLength(length);
    }

    public int getContentLength() {
        return contentLength;
    }

    @Override
    public void setContentType(String type) {
        this.contentType = type;
        super.setContentType(type);
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    private static class FilterServletOutputStream extends ServletOutputStream {
        private DataOutputStream stream;

        public FilterServletOutputStream(OutputStream output) {
            stream = new DataOutputStream(output);
        }

        public void write(int b) throws IOException {
            stream.write(b);
        }

        @Override
        public void write(byte[] b) throws IOException {
            stream.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            stream.write(b, off, len);
        }

		@Override
		public boolean isReady() {
			return true;
		}

		@Override
		public void setWriteListener(final WriteListener writeListener) {
			throw new UnsupportedOperationException("write listening is not implemented");
		}
    }
}
