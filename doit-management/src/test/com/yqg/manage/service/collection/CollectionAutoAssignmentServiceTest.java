package com.yqg.manage.service.collection;

import com.yqg.ManageApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ManageApplication.class)
@WebAppConfiguration
@Slf4j
public class CollectionAutoAssignmentServiceTest {

    @Autowired
    private CollectionAutoAssignmentService collectionAutoAssignmentService;

    @Test
    public void systemAutoAssignment() {
        collectionAutoAssignmentService.systemAutoAssignment();
    }
}
