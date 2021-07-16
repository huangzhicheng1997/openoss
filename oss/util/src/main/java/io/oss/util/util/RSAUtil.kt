package io.oss.util.util

import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher

object RSAUtil {


    /**
     * 随机生成密钥对
     * @throws NoSuchAlgorithmException
     */
    @Throws(NoSuchAlgorithmException::class)
    fun genKeyPair() {
        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
        val keyPairGen = KeyPairGenerator.getInstance("RSA")
        // 初始化密钥对生成器，密钥大小为96-1024位
        keyPairGen.initialize(1024, SecureRandom())
        // 生成一个密钥对，保存在keyPair中
        val keyPair = keyPairGen.generateKeyPair()
        val privateKey = keyPair.private as RSAPrivateKey // 得到私钥
        val publicKey = keyPair.public as RSAPublicKey // 得到公钥
    }

    /**
     * RSA公钥加密
     *
     * @param str
     * 加密字符串
     * @param publicKey
     * 公钥
     * @return 密文
     * @throws Exception
     * 加密过程中的异常信息
     */
    @JvmStatic
    @Throws(Exception::class)
    fun encrypt(str: String, publicKey: String?): String {
        //base64编码的公钥
        val decoded = Base64.getDecoder().decode(publicKey)
        val pubKey = KeyFactory.getInstance("RSA").generatePublic(X509EncodedKeySpec(decoded)) as RSAPublicKey
        //RSA加密
        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.ENCRYPT_MODE, pubKey)
        return Base64.getEncoder().encodeToString(cipher.doFinal(str.toByteArray(charset("UTF-8"))))
    }

    /**
     * RSA私钥解密
     *
     * @param str
     * 加密字符串
     * @param privateKey
     * 私钥
     * @return 铭文
     * @throws Exception
     * 解密过程中的异常信息
     */
    @JvmStatic
    @Throws(Exception::class)
    fun decrypt(str: String, privateKey: String?): String {
        //64位解码加密后的字符串
        val inputByte = Base64.getDecoder().decode(str.toByteArray(charset("UTF-8")))
        //base64编码的私钥
        val decoded = Base64.getDecoder().decode(privateKey)
        val priKey = KeyFactory.getInstance("RSA").generatePrivate(PKCS8EncodedKeySpec(decoded)) as RSAPrivateKey
        //RSA解密
        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.DECRYPT_MODE, priKey)
        return String(cipher.doFinal(inputByte))
    }


    @JvmStatic
    fun main(args: Array<String>) {
        val mutableListOf = mutableListOf(1, 2, 3, 4, 5)
        mutableListOf.forEach(fun(value: Int) {
            if (value==2){
                return
            }
        })
        println("xxx")
    }

}