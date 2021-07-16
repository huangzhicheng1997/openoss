package io.oss.util.protobuf;

/**
 * @author zhicheng
 * @date 2021-05-05 15:08
 */
public class PBCommandConvert {

    public static PBCommandAdaptor convert(PBHeader pbHeader, PBBody pbBody) {
        return new PBCommandAdaptor(pbHeader, pbBody);
    }
}
