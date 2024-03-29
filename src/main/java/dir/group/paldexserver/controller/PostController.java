package dir.group.paldexserver.controller;

import dir.group.paldexserver.dto.PostDTO;
import dir.group.paldexserver.entity.PostEntity;
import dir.group.paldexserver.interfaces.PostWithFilePathProjection;
import dir.group.paldexserver.service.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;


import java.io.File;
import java.util.Collections;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/post")
public class PostController {

    private final Environment env;
    private final PostService postService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public PostController(PostService postService, Environment env) {
        this.postService = postService;
        this.env = env;
    }

    @GetMapping("")
    public ResponseEntity<List<PostEntity>> getPost(@RequestParam("id") long id) {
        return postService.getPostById(id);
    }

    @GetMapping("/tag")
    public ResponseEntity<Object> getPostsByTag(
            @RequestParam("id") String tag,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size) {
        return postService.getPostsByTag(tag, page, size);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size) {
        return postService.getAllPosts(page, size);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getTotalPostCount() {
        long totalPostCount = postService.getTotalPostCount();
        return ResponseEntity.ok(totalPostCount);
    }

    @PostMapping("/set")
    public ResponseEntity<String> createPost(@RequestBody PostDTO postDTO) {
        try {
            postService.savePostWithTransaction(postDTO);
            return new ResponseEntity<>("Post created successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating post: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId) {
        try {
            // Your logic to delete the post with the given postId
            postService.deletePostWithFiles(postId);
            return ResponseEntity.ok("Post deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting post: " + e.getMessage());
        }
    }

    @PostMapping("/addImage")
    public ResponseEntity<List<String>> addImage(@RequestParam("img") List<MultipartFile> files) {
        try {
            List<String> uploadedFileNames = postService.storeFiles(files);
            return new ResponseEntity<>(uploadedFileNames, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error uploading image: {}", e.getMessage(), e);
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delImage")
    public ResponseEntity<String> delImage(@RequestParam List<String> filenames) {
        String uploadFolderPath = env.getProperty("server.uploadPath");;
        try {
            for (String filename : filenames) {
                String filePath = uploadFolderPath + File.separator + filename;

                File file = new File(filePath);

                if (file.exists()) {
                    if (file.delete()) {
                        logger.info("File deleted successfully:  " + filename);
                    } else {
                        logger.info("Failed to delete file: " + filename);
                    }
                } else {
                    logger.info("File not found: " + filePath);
                }
            }
            return new ResponseEntity<>("Images deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting images: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/update")
    public ResponseEntity<String> updatePost(@RequestBody PostDTO postDTO) {
        try {
            postService.savePostWithTransaction(postDTO);
            return new ResponseEntity<>("Post created successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating post: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
