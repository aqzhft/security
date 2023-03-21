package cc.powind.security.token.model;

import java.awt.image.BufferedImage;

public class ImageToken extends AbstractToken {

    private BufferedImage image;

    public ImageToken() {
    }

    public ImageToken(String applyId, String sessionId, String code, Long timeout, BufferedImage image) {
        super(applyId, sessionId, code, timeout);
        this.image = image;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }
}
