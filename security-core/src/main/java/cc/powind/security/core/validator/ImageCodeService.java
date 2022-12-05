package cc.powind.security.core.validator;

import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.util.Config;
import org.springframework.http.MediaType;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Properties;

public class ImageCodeService extends AbstractValidateCodeService<ImageCode> {

    /**
     * 宽度
     */
    private int width = 200;

    /**
     * 高度
     */
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
    protected ImageCode doCreate(ServletWebRequest webRequest) {

        int width = ServletRequestUtils.getIntParameter(webRequest.getRequest(), "width", this.width);
        int height = ServletRequestUtils.getIntParameter(webRequest.getRequest(), "height", this.height);
        int len = ServletRequestUtils.getIntParameter(webRequest.getRequest(), getValidateCodeParameterLen(), getLen());

        Producer producer = getImageProducer(width, height, len);
        String code = producer.createText();

        return new ImageCode(getValidateCodeId(webRequest), code, getTimeout(), producer.createImage(code));
    }

    @Override
    protected void send(ImageCode code, ServletWebRequest request) {
        try {
            HttpServletResponse response = request.getResponse();
            response.setContentType(MediaType.IMAGE_JPEG_VALUE);

            ImageIO.write(code.getImage(), "jpg", response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException("send image code fail", e);
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
