package com.sunfintech.virtual.machine.handle;

public class StackOverFlowTest {

    private int stackLength = 1;
    
    /**
     * 无限递归,大量创建栈帧导致栈溢出
     */
    public void stackLeak() {
        this.setStackLength(this.getStackLength() + 1);
        stackLeak();
    }
    
    /**
     * 创建一个巨大无比的栈帧,导致直接Stack Overflow
     */
    public static void test() {
        long unused1,unused2,unused3,unused4,unused5,unused6,unused7,unused8,unused9,unused10,
        unused11,unused12,unused13,unused14,unused15,unused16,unused17,unused18,unused19,unused20,
        unused21,unused22,unused23,unused24,unused25,unused26,unused27,unused28,unused29,unused30,
        unused31,unused32,unused33,unused34,unused35,unused36,unused37,unused38,unused39,unused40,
        unused41,unused42,unused43,unused44,unused45,unused46,unused47,unused48,unused49,unused50,
        unused51,unused52,unused53,unused54,unused55,unused56,unused57,unused58,unused59,unused60,
        unused61,unused62,unused63,unused64,unused65,unused66,unused67,unused68,unused69,unused70,
        unused71,unused72,unused73,unused74,unused75,unused76,unused77,unused78,unused79,unused80,
        unused81,unused82,unused83,unused84,unused85,unused86,unused87,unused88,unused89,unused90,
        unused91,unused92,unused93,unused94,unused95,unused96,unused97,unused98,unused99,unused100,
        unused101,unused102,unused103,unused104,unused105,unused106,unused107,unused108,unused109,unused110,
        unused111,unused112,unused113,unused114,unused115,unused116,unused117,unused118,unused119,unused120,
        unused121,unused122,unused123,unused124,unused125,unused126,unused127,unused128,unused129,unused130;
        
        test();
    }
    
    /**
     * 无限递归,导致栈帧无法继续创建,导致Stack Overflow
     * 
     * 说明虚拟机无法自动扩展栈内存
     * 
     * -Xss128K
     * @param args
     */
    public static void main(String[] args) {
        StackOverFlowTest stackOverFlowTest = new StackOverFlowTest();
        try {
            stackOverFlowTest.stackLeak();
        } catch (Error e) {
            System.out.println(e);
            throw e;
        }
    }

    public int getStackLength() {
        return stackLength;
    }

    public void setStackLength(int stackLength) {
        this.stackLength = stackLength;
    }
}
