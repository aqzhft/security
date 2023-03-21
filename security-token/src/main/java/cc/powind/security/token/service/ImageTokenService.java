package cc.powind.security.token.service;

import cc.powind.security.token.model.ImageToken;
import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.util.Config;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Properties;

public class ImageTokenService extends AbstractTokenService<ImageToken> {

    private int width = 200;

    private int height = 50;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    protected ImageToken doCreate(HttpServletRequest request) {

        Producer producer = getImageProducer(getWidth(request), getHeight(request), getDefaultLen(request));
        String code = producer.createText();

        return new ImageToken(getApplyId(request), getValidateCodeId(request), code, getTimeout(), producer.createImage(code));
    }

    private int getWidth(HttpServletRequest request) {
        String width = request.getParameter("width");
        if (StringUtils.isNotBlank(width) && NumberUtils.isDigits(width)) {
            return NumberUtils.toInt(width);
        }
        return this.width;
    }

    private int getHeight(HttpServletRequest request) {
        String height = request.getParameter("height");
        if (StringUtils.isNotBlank(height) && NumberUtils.isDigits(height)) {
            return NumberUtils.toInt(height);
        }
        return this.height;
    }

    @Override
    protected void send(ImageToken code, HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setContentType("image/jpeg");
            ImageIO.write(code.getImage(), "jpg", response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException("send image code failed", e);
        }
    }

    protected Producer getImageProducer(int width, int height, int len) {
        Properties props = new Properties();
        props.setProperty(Constants.KAPTCHA_IMAGE_WIDTH, String.valueOf(width));
        props.setProperty(Constants.KAPTCHA_IMAGE_HEIGHT, String.valueOf(height));
        props.setProperty(Constants.KAPTCHA_TEXTPRODUCER_CHAR_LENGTH, String.valueOf(len));
        return new Config(props).getProducerImpl();
    }
}
