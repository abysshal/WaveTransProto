#History

===Log===

20130313

#####进展
1. 使用AnalyzerTest2进行测试.
2. ./test/AC3_S5570_r/wavein_AC3_S5570_r_33.wav 为android端录制的AC3进行反向后的wav文件,含有33段有效数据,现能正确检测出33段.
3. 直接将录制得到的16bit signed mono的wav数据进行解码失败;defy和K7端无法得到正常录音波形. 

#####待办
1. 实现将录音和解码同时在android端进行.
2. 测试先转为8bit再解码与直接16bit解码 效率比较
3. 查清defy和K7无法录音的原因
4. 测试android端录音解码的鲁棒性

===

20130312

#####进展
1. 使用DynamicAverageAnalyzer和AnalyzerTest4进行测试.
2. ./test/AC3_S5570_pro/wavein_AC3_S5570_0_pro.wav 为PC端录制的AC3输入文件,含有25段有效数据,现能正确检测出25段.
3. 其他为android端录制文件,无法有效检测出任何一段.由于源声输入文件为16bit编码,使用Amadeus转码成8bit后仍然无法解码.无论是16bit还是8bit的文件,看波形和PC端的相去甚远.

#####待办
1. 重新在android端录制源文件,以求更好的波形.
2. 研究16bit的编码方式,就大小头/是否带符号等问题做测试确认.
3. 改进检测算法(很难..)在1.能解决的情况下,任何简单算法都可以. 1.不能解决,多好的算法都难.