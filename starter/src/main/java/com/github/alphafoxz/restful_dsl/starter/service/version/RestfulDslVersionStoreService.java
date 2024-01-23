package com.github.alphafoxz.restful_dsl.starter.service.version;

import cn.hutool.core.io.FileUtil;
import com.github.alphafoxz.restful_dsl.starter.RestfulDslConstants;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class RestfulDslVersionStoreService {
    private final GenRestful genRestfulPart = new GenRestful();

    public static class GenRestful implements VersionStore {
        private final File file = FileUtil.file(RestfulDslConstants.SDK_VERSION_PATH + File.separator + "restful_dsl_gen.json");

        {
            init();
        }

        @Override
        @NonNull
        public File getFile() {
            return file;
        }
    }

    public GenRestful genRestfulStore() {
        return genRestfulPart;
    }
}
