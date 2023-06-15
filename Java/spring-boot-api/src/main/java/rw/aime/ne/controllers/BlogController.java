package rw.aime.ne.controllers;

import io.swagger.models.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rw.aime.ne.models.Blog;
import rw.aime.ne.payload.ApiResponse;
import rw.aime.ne.payload.ErrorResponse;
import rw.aime.ne.repositories.BlogRepository;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class BlogController {
    @Autowired
    BlogRepository blogRepository;

    @GetMapping("/blogs")
    public ResponseEntity<ApiResponse> getAllBlogs(@RequestParam(required = false) String title) {
        try {
            List<Blog> blogs = new ArrayList<Blog>();
            if (title == null)
                blogs.addAll(blogRepository.findAll());
            else
                blogs.addAll(blogRepository.findByTitleContaining(title));

            if (blogs.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true, blogs));
        }catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/blog/{id}")
    public ResponseEntity<ApiResponse> getBlogById(@PathVariable("id") long id) {
        Optional<Blog> blogData = blogRepository.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, blogData.map(blog -> new ResponseEntity<>(blog, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND))));
    }
    @PostMapping("/blogs")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<ApiResponse> createBlog(@RequestBody Blog blog) {
        try {
            Blog _blog = blogRepository.save(new Blog(blog.getTitle(), blog.getDescription(), false));
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true, _blog));

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/blog/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<ApiResponse> updateBlog(@PathVariable("id") long id, @RequestBody Blog blog) {
        Optional<Blog> blogData = blogRepository.findById(id);
        if(blogData.isPresent()) {
            Blog _blog = blogData.get();
            _blog.setTitle(blog.getTitle());
            _blog.setDescription(blog.getDescription());
            _blog.setPublished(blog.isPublished());
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, blogRepository.save(_blog)));
        }else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @DeleteMapping("/blog/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<HttpStatus> deleteBlog(@PathVariable("id") long id) {
        try {
            blogRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/blogs")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<HttpStatus> deleteAllBlogs(@PathVariable("id") long id) {
        try {
            blogRepository.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
