/*
 *  
 *
 * The jmind-pigg Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package jmind.pigg.descriptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import jmind.base.util.DataUtil;
import jmind.base.util.reflect.ClassUtil;
import jmind.pigg.annotation.Cache;
import jmind.pigg.annotation.CacheIgnored;
import jmind.pigg.annotation.DB;
import jmind.pigg.annotation.GeneratedId;
import jmind.pigg.annotation.SQL;
import jmind.pigg.annotation.Sharding;
import jmind.pigg.annotation.UseSqlGenerator;
import jmind.pigg.exception.DescriptionException;
import jmind.pigg.util.Strings;
import jmind.pigg.util.logging.InternalLogger;
import jmind.pigg.util.logging.InternalLoggerFactory;


/**
 * 方法描述
 *
 * @author xieweibo
 */
public class MethodDescriptor {

    private final static InternalLogger logger = InternalLoggerFactory.getInstance(MethodDescriptor.class);

    private final String name;
    private final Class<?> daoClass;
    private final ReturnDescriptor returnDescriptor;
    private final List<ParameterDescriptor> parameterDescriptors;
    private final String sql;

    private MethodDescriptor(String name, Class<?> daoClass, ReturnDescriptor returnDescriptor,
            List<ParameterDescriptor> parameterDescriptors) {
        this.name = name;
        this.daoClass = daoClass;
        this.returnDescriptor = returnDescriptor;
        this.parameterDescriptors = Collections.unmodifiableList(parameterDescriptors);
        this.sql = getSQL0();
    }

    public static MethodDescriptor create(String name, Class<?> daoClass, ReturnDescriptor returnDescriptor,
            List<ParameterDescriptor> parameterDescriptors) {
        return new MethodDescriptor(name, daoClass, returnDescriptor, parameterDescriptors);
    }

    public String getName() {
        return name;
    }

    public Class<?> getDaoClass() {
        return daoClass;
    }

    public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
        return getAnnotation(annotationType) != null;
    }

    @Nullable
    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        return returnDescriptor.getAnnotation(annotationType);
    }

    public Type getReturnType() {
        return returnDescriptor.getType();
    }

    public Class<?> getReturnRawType() {
        return returnDescriptor.getRawType();
    }

    public List<Annotation> getAnnotations() {
        return returnDescriptor.getAnnotations();
    }

    public ReturnDescriptor getReturnDescriptor() {
        return returnDescriptor;
    }

    public List<ParameterDescriptor> getParameterDescriptors() {
        return parameterDescriptors;
    }

    // 缓存sql语句
    public String getSQL() {
        return sql;
    }

    private String getSQL0() {
        SQL sqlAnno = getAnnotation(SQL.class);
        String sql;
        if (sqlAnno != null) {
            sql = sqlAnno.value();
        } else {
            UseSqlGenerator useSqlGeneratorAnno = getAnnotation(UseSqlGenerator.class);
            if (useSqlGeneratorAnno == null) {
                throw new DescriptionException(
                        "each method expected one of @SQL or @UseSqlGenerator annotation but not found");
            }
            SqlGenerator sqlGenerator = ClassUtil.instantiateClass(useSqlGeneratorAnno.value());
            sql = sqlGenerator.generateSql(this);
        }
        if (DataUtil.isEmpty(sql)) {
            throw new DescriptionException("sql is null or empty");
        }
        if (logger.isDebugEnabled()) {
            logger.debug(sql);
        }
        return sql;
    }

    @Nullable
    public String getGlobalTable() {
        DB dbAnno = getAnnotation(DB.class);
        if (dbAnno == null) {
            throw new DescriptionException("dao interface expected one @DB " + "annotation but not found");
        }
        String table = null;
        if (DataUtil.isNotEmpty(dbAnno.table())) {
            table = dbAnno.table();
        }
        return table;
    }

    public String getDataSourceFactoryName() {
        DB dbAnno = getAnnotation(DB.class);
        if (dbAnno == null) {
            throw new DescriptionException("dao interface expected one @DB " + "annotation but not found");
        }
        return dbAnno.name();
    }

    @Nullable
    public Sharding getShardingAnno() {
        return getAnnotation(Sharding.class);
    }

    public boolean isUseCache() {
        CacheIgnored cacheIgnoredAnno = getAnnotation(CacheIgnored.class);
        Cache cacheAnno = getAnnotation(Cache.class);
        return cacheAnno != null && cacheIgnoredAnno == null;
    }

    public boolean isReturnGeneratedId() {
        return isAnnotationPresent(GeneratedId.class);
            //    || (name != null && name.contains(GeneratedId.class.getSimpleName()));
    }

}
