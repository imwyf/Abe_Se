package com.duwei.text.transportable;

import com.duwei.text.IndexCiphertext;
import it.unisa.dia.gas.jpbc.Element;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @BelongsProject: Abe_Se
 * @BelongsPackage: com.duwei.text.transportable
 * @Author: duwei
 * @Date: 2023/5/10 15:24
 * @Description: 可传输的索引密文
 */
@Data
@NoArgsConstructor
public class TransportableIndexCiphertext implements Serializable {
    private List<byte[]> W_list;
    private byte[] W1;
    private byte[] W2;
    private byte[] W3;

    public TransportableIndexCiphertext(IndexCiphertext indexCiphertext) {
        W1 = indexCiphertext.getW1().toBytes();
        W2 = indexCiphertext.getW2().toBytes();
        W3 = indexCiphertext.getW3().toBytes();
        W_list = indexCiphertext.getW_list().stream().map(Element::toBytes).collect(Collectors.toList());
    }
}
