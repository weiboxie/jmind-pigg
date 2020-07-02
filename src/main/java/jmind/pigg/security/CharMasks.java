package jmind.pigg.security;

/**
 * description:
 *
 * @author weibo.xie
 * @date : create in 2:25 下午 2020/7/2
 */
public class CharMasks {

    private int size;
    private boolean[] masks;

    public CharMasks(int size) {
        if (size < 0) {
            throw new RuntimeException("Illegal argument exception: " +
                    "size should be not be less than zero.");
        }
        this.setSize(size);
        this.setMasks(new boolean[size]);
    }

    public void addCharToMasks(char c) {
        if (c >= 0 && c < size) {
            masks[c] = true;
        }
    }

    public void addCharToMasks(String s) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= 0 && c < size) {
                masks[c] = true;
            }
        }
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean[] getMasks() {
        return masks;
    }

    public void setMasks(boolean[] masks) {
        this.masks = masks;
    }

}