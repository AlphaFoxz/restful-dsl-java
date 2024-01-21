package demo.restful.biz_test.controller;

import demo.restful.biz_test.gen.restful.apis.TestApi;
import demo.restful.biz_test.gen.restful.dtos.TestADto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController implements TestApi {
    @Override
    public ResponseEntity<TestADto> query(Long id) {
        TestADto result = new TestADto();
        result.setId(id).setName("Hello!");
        return ResponseEntity.status(OK_200).body(result);
    }
}
