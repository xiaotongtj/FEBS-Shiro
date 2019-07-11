package cc.mrbird.febs.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

/**
 * @author MrBird
 */
public class MD5Util {

    protected MD5Util() {

    }

    private static final String ALGORITH_NAME = "md5";

    private static final int HASH_ITERATIONS = 5;

    public static String encrypt(String username, String password) {
        //1.将用户名作为盐值
        String source = StringUtils.lowerCase(username);
        //2.
        password = StringUtils.lowerCase(password);
        //3.加密类型 + 前端密码 + 盐值（ByteSource.Util.bytes） + 加密次数
        return new SimpleHash(ALGORITH_NAME, password, ByteSource.Util.bytes(source), HASH_ITERATIONS).toHex();
    }
}
