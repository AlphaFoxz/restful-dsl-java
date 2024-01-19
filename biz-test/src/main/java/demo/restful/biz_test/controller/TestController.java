package demo.restful.biz_test.controller;

import demo.restful.biz_test.gen.restful.apis.TestApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController implements TestApi {
    @Override
    public ResponseEntity<String> query(Long id) {
        return ResponseEntity.ok("Hello! id=" + id);
    }
}
