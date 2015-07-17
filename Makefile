JAVA_SRC:=src/main/java
CPP_SRC:=src/main/resources/cpp-gen
TARGET:=target/scala-2.10

CC:=gcc

jniheader: $(CPP_SRC)/NumaNative.h

compile: $(wildcard $(JAVA_SRC)/xerial/jnuma/*.java)
	bin/sbt compile

native: src/main/resources/xerial/jnuma/native/linux/x86_64/libjnuma.so

$(CPP_SRC)/NumaNative.h: $(JAVA_SRC)/xerial/jnuma/NumaNative.java compile
	javah -classpath $(TARGET)/classes -o $@ xerial.jnuma.NumaNative

$(TARGET)/lib/NumaNative.o: $(CPP_SRC)/NumaNative.c
	@mkdir -p $(@D)
	$(CC) -O2 -fPIC -m64 -I include -I $(CPP_SRC) -c $< -o $@

$(TARGET)/lib/libjnuma.so : $(TARGET)/lib/NumaNative.o
	$(CC) -O2 -shared -static-libgcc -fPIC -fvisibility=hidden -m64 -L/usr/lib64 -lnuma -o $@ $+
	strip $@

src/main/resources/xerial/jnuma/native/linux/x86_64/libjnuma.so : $(TARGET)/lib/libjnuma.so
	@mkdir -p $(@D)
	cp $< $@

clean-native:
	rm -f $(TARGET)/lib/*

