package rw.aime.ne.controllers;

import io.swagger.models.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rw.aime.ne.models.Blog;
import rw.aime.ne.repositories.BlogRepository;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/blogs")
public class BlogController {
    @Autowired
    BlogRepository blogRepository;

    @GetMapping("")
    public ResponseEntity<List<Blog>> getAllBlogs(@RequestParam(required = false) String title) {
        try {
            List<Blog> blogs = new ArrayList<Blog>();
            if (title == null)
                blogs.addAll(blogRepository.findAll());
            else
                blogs.addAll(blogRepository.findByTitleContaining(title));

            if (blogs.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(blogs, HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/blog/{id}")
    public ResponseEntity<Blog> getBlogById(@PathVariable("id") long id) {
        Optional<Blog> blogData = blogRepository.findById(id);
        return blogData.map(blog -> new ResponseEntity<>(blog, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    @PostMapping("")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<Blog> createBlog(@RequestBody Blog blog) {
        try {
            Blog _blog = blogRepository.save(new Blog(blog.getTitle(), blog.getDescription(), false));
            return new ResponseEntity<>(_blog, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/blog/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<Blog> updateBlog(@PathVariable("id") long id, @RequestBody Blog blog) {
        Optional<Blog> blogData = blogRepository.findById(id);
        if(blogData.isPresent()) {
            Blog _blog = blogData.get();
            _blog.setTitle(blog.getTitle());
            _blog.setDescription(blog.getDescription());
            _blog.setPublished(blog.isPublished());
            return new ResponseEntity<>(blogRepository.save(_blog), HttpStatus.OK);
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

    @DeleteMapping("")
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