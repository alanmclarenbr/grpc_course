package blog.client;

import com.blog.Blog;
import com.blog.BlogId;
import com.blog.BlogServiceGrpc;
import com.google.protobuf.Empty;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class BlogClient {

    private BlogServiceGrpc.BlogServiceBlockingStub blogServiceBlockingStub;
    private BlogServiceGrpc.BlogServiceStub blogServiceStub;

    public BlogClient(BlogServiceGrpc.BlogServiceBlockingStub blogServiceBlockingStub, BlogServiceGrpc.BlogServiceStub blogServiceStub) {
        this.blogServiceBlockingStub = blogServiceBlockingStub;
        this.blogServiceStub = blogServiceStub;
    }

    public String createBlog(String author, String title, String content) {
        Blog request = Blog.newBuilder()
                .setAuthor(author)
                .setContent(content)
                .setTitle(title)
                .build();

        BlogId blogId = blogServiceBlockingStub.createBlog(request);
        return blogId.getId();
    }

    public Blog readBlog(String blogId) {
        BlogId id = BlogId.newBuilder()
                .setId(blogId)
                .build();

        return blogServiceBlockingStub.readBlog(id);
    }

    public void deleteBlog(String blogId) {
        BlogId id = BlogId.newBuilder()
                .setId(blogId)
                .build();

        blogServiceBlockingStub.deleteBlog(id);
    }

    public void updateBlog(String id, String author, String title, String content) {
        Blog request = Blog.newBuilder()
                .setId(id)
                .setAuthor(author)
                .setTitle(title)
                .setContent(content)
                .build();

        blogServiceBlockingStub.updateBlog(request);
    }

    public List<Blog> listBlogs() {
        return newArrayList(blogServiceBlockingStub.listBlogs(Empty.getDefaultInstance()));
    }
}
