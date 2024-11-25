package com.qry.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.springdoc.api.annotations.ParameterObject;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.qry.model.QUser;
import com.qry.model.QUserDto;
import com.qry.model.User;
import com.qry.repository.UserRepository;
import com.querydsl.core.types.Predicate;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
public class UserController {

    @GetMapping("/api/v1/test")
//    @Parameter(name ="predicate", schema = @Schema(type = "object"))
    public String getCasesByQuery(
//        @Parameter(in = ParameterIn.QUERY, name = "filter", description = "Querydsl predicate for filtering cases", schema = @Schema(type = "object", name = "predicate"))
//        @ParameterObject
//        @QuerydslPredicate(root = User.class, bindings = UserRepository.class) Predicate predicate,
        @RequestBody QUserDto userDto,
        @PageableDefault Pageable pageable) {
        return userDto.toString();
    }
    
    @PostMapping("api/v1/upload")
    public void upload(@RequestParam("pdf_file") MultipartFile file
        , @RequestParam("docId") String docId
        , @RequestParam("fileSize") String fileSize        
        ) throws IOException {
        
        System.out.println(file.getOriginalFilename());
        System.out.println(file.getResource());
        System.out.println("docId: " + docId);
        System.out.println("fileSize: " + fileSize);
        InputStream inputStream = file.getResource().getInputStream();
        File file2 = new File("/home/digitaldots/Documents/test.pdf");
        if (!file2.exists()) {
            file2.createNewFile();
        }
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file2));
        
        byte[] dataBuffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(dataBuffer, 0, 1024)) != -1) {
            bufferedOutputStream.write(dataBuffer, 0, bytesRead);
        }
        bufferedOutputStream.close();
    }

}
