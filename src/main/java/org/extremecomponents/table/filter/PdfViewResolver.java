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

import static org.apache.xmlgraphics.util.MimeConstants.MIME_PDF;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.xml.XMLConstants;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.FopFactoryBuilder;
import org.extremecomponents.table.core.Preferences;

/**
 * @author Jeff Johnston
 */
public class PdfViewResolver implements ViewResolver {
    private static final String USERCONFIG_LOCATION = "exportPdf.userconfigLocation";

    public void resolveView(ServletRequest request, ServletResponse response, Preferences preferences, Object viewData) throws Exception {
        InputStream is = new ByteArrayInputStream(((String) viewData).getBytes(StandardCharsets.UTF_8));

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        final TransformerFactory tfact = TransformerFactory.newInstance();
        tfact.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        tfact.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");

        URL configUrl = this.getClass().getResource("fop.xconf");
        String userconfigLocation = preferences.getPreference(USERCONFIG_LOCATION);
        if (userconfigLocation != null) {
        	configUrl = this.getClass().getResource(userconfigLocation);
        }

        final FopFactory fopFactory = new FopFactoryBuilder(configUrl.toURI())
        		.build();
        final Fop fop = fopFactory.newFop(MIME_PDF, out);

        final Transformer transformer = tfact.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, StandardCharsets.UTF_8.name());
        final StreamSource streamSource = new StreamSource(is);
        transformer.transform(streamSource, new SAXResult(fop.getDefaultHandler()));

        final byte[] contents = out.toByteArray();
        response.setContentLength(contents.length);
        response.getOutputStream().write(contents);
    }
}
