/*
 * Copyright 2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vafer.jdeb.maven;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import java.util.List;
import java.util.StringTokenizer;
import org.vafer.jdeb.DataConsumer;
import org.vafer.jdeb.DataProducer;
import org.vafer.jdeb.producers.DataProducerArchive;
import org.vafer.jdeb.producers.DataProducerDirectory;

/**
 * Maven "data" elment acting as a factory for DataProducers.
 * So far Archive and Directory producers are supported.
 * Both support the usual ant pattern set matching.
 * 
 * @author Bryan Sant <bryan.sant@gmail.com>
 */
public final class Data implements DataProducer {

    /**
     * @parameter expression="${src}"
     * @required
     */
    private File src;

    /**
     * @parameter expression="${includes}" alias="includes"
     */
    public void setIncludes(String includes) {
        includePatterns = splitPatterns(includes);
    }
    private String[] includePatterns;

    /**
     * @parameter expression="${excludes}" alias="excludes"
     */
    public void setExcludes(String excludes) {
        excludePatterns = splitPatterns(excludes);
    }
    private String[] excludePatterns;

    /**
     * @parameter expression="${mapper}"
     */
    private Mapper[] mapper;

    public String[] splitPatterns(String patterns) {
        String[] result = null;
        if (patterns != null && patterns.length() > 0) {
            List tokens = new ArrayList();
            StringTokenizer tok = new StringTokenizer(patterns, ", ", false);
            while (tok.hasMoreTokens()) {
                tokens.add(tok.nextToken());
            }
            result = (String[]) tokens.toArray(new String[tokens.size()]);
        }
        return result;
    }

    public void produce(final DataConsumer pReceiver) throws IOException {
        if (!src.exists()) {
            throw new FileNotFoundException("Data source not found : " + src);
        }

        org.vafer.jdeb.mapping.Mapper[] mappers = new org.vafer.jdeb.mapping.Mapper[mapper.length];
        for (int i = 0; i < mapper.length; i++) {
            mappers[i] = mapper[i].createMapper();
        }

        if (src.isFile()) {
            new DataProducerArchive(
                    src,
                    includePatterns,
                    excludePatterns,
                    mappers).produce(pReceiver);
        } else {
            new DataProducerDirectory(
                    src,
                    includePatterns,
                    excludePatterns,
                    mappers).produce(pReceiver);
        }
    }
}
