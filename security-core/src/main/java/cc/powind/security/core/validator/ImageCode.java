package cc.powind.security.core.validator;

import java.awt.image.BufferedImage;

/**
 * 图片验证码
 */
public class ImageCode extends BaseValidateCode {

    /**
     * 图片流
     */
    private BufferedImage image;

    public ImageCode() {
    }

    public ImageCode(String sessionId, String code, Long timeout, BufferedImage image) {
        super(sessionId, code, timeout);
        this.image = image;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }
}
