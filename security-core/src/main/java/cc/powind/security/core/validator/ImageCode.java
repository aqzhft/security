package cc.powind.security.core.validator;

import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;

/**
 * 图片验证码
 */
public class ImageCode extends BaseValidateCode {

    /**
     * 回显到浏览器
     */
    private HttpServletResponse response;

    /**
     * 图片流
     */
    private BufferedImage image;

    public ImageCode() {
    }

    public ImageCode(String id, String code, Long expireLength, HttpServletResponse response, BufferedImage image) {
        super(id, code, expireLength);
        this.response = response;
        this.image = image;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }
}
