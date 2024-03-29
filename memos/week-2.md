[TOC]

# Day1		

---

## java.io

```java
//package - import - class 선언식으로 온다

package javaclass;

import java.io.OutputStream;

public class Test076 {
    public static void main(String[] args) {
        OutputStream out = null;
    }
}

//패키지가 있는 폴더 javac -d . Test076.java
//java -classpath . javaclass.Test076
```

```java
package javaclass;

import java.io.FileNotFoundException;
import java.io.FileOutputStream; //import의 단위는 class, 다른패키지에 있는 class가져다 쓸때 필요
import java.io.OutputStream;

public class Test076 {
    public static void main(String[] args){
        try {//FileOutputStram의 생성자가 에러를 던지고 있기때문에 이걸 호출하는 코드는 try-catch문에 적어야함
            OutputStream out = new FileOutputStream("a.dat");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
```

throws XXXException 형태로 선언된 함수는 XXXException이 깐깐할 경우에 해당 예외를 처리할 수있는 try-catch 영역 안에서 사용해야한다.

깐깐하다 : 컴파일시 에러가 난다.

```java
 try {
            OutputStream out = new FileOutputStream("a.dat");
        } catch (IOException e) {
            e.printStackTrace();
        }
```

FileNotFoundException를 IOException로 바꿔도 IOException이 조상이기 떄문에 자손의 인스턴스를 가리킬 수 있고 때문에 에러가 나지않는다.

``` java
package javaclass;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Test076 {
    public static void main(String[] args) throws IOException {
            OutputStream out = new FileOutputStream("a.dat");
    }//발생되는 에러를 잡으면 에러가 일어난게 아님 프로그램 안죽음
}
```

main(String[] args) throws IOException 이 문법 에러가 아닌 이유

1. 에러는 발생시에 잡아주면 에러 발생 안된걸로 한다. (일사부재리)

2. 에러가 발생할 수 있으면 그 사실을 명시하면 된다.

   (생상자 호출한 쪽이 아니라 main을 호출한 쪽이 처리한다.)
   ex) 사원이 사고치는데 대리는 그책임을 부장에게 넘기고 부장이 처리한다.

``` java
public class Test077 {
    public static void main(String[] args)throws IOException {
            OutputStream out = new FileOutputStream("a.dat");
            out.write(65);  //A
            out.write(66);  //B  참고로 256 집어넣으면 0이 들어가고 -1을 넣으면 255가 들어감
            out.write(67);  //C
            out.close();
        }
}
```

+ FileOutputStream : 파일에 저장하는 방법을 제공한다.

+ OutputStream : 내보내는 방법을추상화 한 클래스(이걸 상속받은 클래스는 이걸로 가리킬 수있고, 이것에 선언 된 것만 쓴다)

+ write : 한번에 1바이트를 내보낸다. 오버라이딩 된 write가 호출된다(api보니까), 오버라이딩 된 write는 매개변수의 값을 a.dat 파일에 저장하게 된다.

+ out.close(); : 내보내는 통로를 닫고 뒤처리를 해 준다.

  `AppleOutputStream extends Outputstream ...` 형태로 선언되어졌다고 셈 치면, 인스턴스만 바꾸면 나머지는 바꿀 필요가 없다.

``` java
package javaclass;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Test079 {
    public static void main(String[] args) throws IOException {
        InputStream in = new FileInputStream("a.dat");
        int a= in.read();
        int b= in.read();
        int c= in.read();
        int d= in.read();
        int e= in.read();
        in.close();

        System.out.println(a);
        System.out.println(b);
        System.out.println(c);
        System.out.println(d);
        System.out.println(e);
    }
}
```

```
결과값 : 65 66 67 -1 -1
```

+ `in.read()` :한 바이트씩 읽어드리는 역할

  더 읽을값이 없을때 -1이 나온다.

### Stream( Input/ Output ) 개념

+ byte단위로 전송
+ 순서대로읽고 순서대로 내보냄
+ 중간부분만 따로읽고 내보내는건 없다. 무조건 처음부터 끝까지!
+ 다 썻으면 반드시 close 호출

### 파일 복사 / 읽기

```java
        // 복사 : dir *.dat로 확인하고 type b.dat로 확인하고
        while (true) {
            int r = in.read();
            if (r == -1){
                break;
            }
            out.write(r);
        }
```

``` java
		//많이 쓰는방법1
        int r = 0;
        while ((r = in.read()) != -1){
            out.write(r);
        }
```

```java
        //많이 쓰는방법2
        int r = 0;
        byte[] buf = new byte[1024 * 8]; //64bit = 8 byte  //1024bit = 1kb
        while ((r = in.read(buf)) != -1){
            out.write(buf, 0, r);
        }
```

+ 버퍼링 : 한꺼번에 읽고 내보낸다
+ `int read(byte[] but)` : buf가 감당 가능한 만큼 읽고, 읽은 데이터 갯수 리턴;
+ `void write(byte[] buf, int s, int r)` : buf의 내용을 내보낸다. s부터 r만큼

## Socket 통신

```java
package javaclass;

import java.io.IOException;
import java.net.Socket;

public class Test083C {
    public static void main(String[] args) throws IOException {
        Socket skt = new Socket("192.168.2.70", 1123);
                // OutputStream으로 상속받은 무언가를 생성하고 리턴. out을 가리킴
        OutputStream out = skt.getOutputStream();

        out.write(65);
        out.flush();

        skt.close();
    }
}
```

네트워크 버퍼 언제 비워지냐 : 버퍼가 꽉찼을 때 , flush(); 쓸때, close();쓸때

```java
package javaclass;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Test083S {
    public static void main(String[] args) throws IOException {
        ServerSocket svr = new ServerSocket(1123);
        System.out.println("Before accept()");
        Socket skt = svr.accept();

        System.out.println("After accept()");

        skt.close();
        svr.close();
    }
}
```

+ 대기하는 쪽 : 서버, 찾아가는 쪽 : 클라이언트

  먼저 서버가 구동한다 - 포트를 물고 구동해야한다. (1123)

  accept : 대기하다가 클라이언트가 찾아오면 소켓을 생성해서 리턴

클라이언트가 찾아가기위해 IP, PORT번호 줌

잘 찾아가지면 서버와 클라이언트 모두 Socket이 생성됨 이 둘은 통신이 가능함

```java

import java.io.*;

public class Test084 {
    public static void main(String[] args) {
        try (// java.io 패키지가 데코레이터 패턴이라는 설계기법으로 구현되었다.
             OutputStream out = new BufferedOutputStream(new FileOutputStream("d.dat"));
             InputStream in = new BufferedInputStream(new FileInputStream("c.dat"));
        ) {
            int r = 0;
            while ((r = in.read()) != -1){
                out.write(r);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

out.write 호출했을때 대상은 FileOutputStream 에서 지정한다.

BufferedOutputStream에서 버퍼링을 제공한다.

두 클래스가 결합된 결과를 만드는데 사용자는 OutputStream에서 지정하는 함수만 사용하면 된다.

```java
package javaclass;

import java.io.*;

public class Test085 {
    public static void main(String[] args) {
        int a, b, c;
        // 이게 왜 깨지는지 ? 4바이트 다 보내는게 아니더라
        try (OutputStream out = new FileOutputStream("d.dat");
             InputStream in = new FileInputStream("d.dat");
        ){
            out.write(10101);
            out.write(10102);
            out.write(10103);

            a = in.read();
            b = in.read();
            c = in.read();

            System.out.println(a);
            System.out.println(b);
            System.out.println(c);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

```java
package javaclass;

import java.io.*;

public class Test085 {
    public static void main(String[] args) {
        String a = null, b = null, c = null;
        // 데코레이터 패턴으로 이 방법을 이해하면 out이 가리키는 대상은
//        FileOutputStream("d.dat")에저장하되 ObjectOutputStream에서 제공하는 방법을 사용하게 된다.
        //ObjectOutputStream은 writeInt writeDouble writeUTF등을 제공 - 전송시에 안깨진다.
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("d.dat"));
        ){
            out.writeUTF("10101");
            out.writeUTF("10102");
            out.writeUTF("10103");

        } catch (Exception e) {
            e.printStackTrace();
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("d.dat"));){
            a = in.readUTF();
            b = in.readUTF();
            c = in.readUTF();

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(a);
        System.out.println(b);
        System.out.println(c);
    }
}
```

```java
package javaclass;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;

public class Test086 {
    // OutputStream InputStream : 전송단위 byte : 바이너리 파일 전송용
    // Reader Writer : 전송단위 Char : 문자로 된 데이터 전송용
    // char는 2바이트이고, 유니코드를 지원한다.
    // 유니코드는 모든 글자를 다 포용하지못한다. (6만자제한)
    // 웬만한 글자는 포용한다. 한글 + 영문 + 중국어 + 아랍어 + 일본어...
    // 확장 가능한 가변길이를 가지는 문자체제를 도입 : UTF-8 (웹 표준)
    public static void main(String[] args) {
        try(
                Writer out = new FileWriter("a.txt")
                ){
            out.write('한');
            out.write('글');
            out.write('林');
        }catch (Exception e){
            e.printStackTrace();
        }

        try(
                Reader in = new FileReader("a.txt");
        ){
            System.out.println((char)in.read());
            System.out.println((char)in.read());
            System.out.println((char)in.read());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
```

### 파일이 있는지

```java
package javaclass;

import java.io.File;

public class Test089 {
    public static void main(String[] args) {
        File f = new File("a.txt");
        boolean b = f.exists();
        System.out.println(b);
        if(b){
            //파일이 길이를 리턴한다. (long 형 자료에 주의)
            System.out.println(f.length());
        }
    }
}
```



## 문제

### 성적처리

```java
package javaclass;

import java.io.*;
class Score<T>{
    String stdId = null;
    String score = null;
    Score<T> next = null;

    Score(String i,String j, Score<T> n){
        stdId = i;
        score = j;
        next = n;
    }
}
class LinkedList<X>{
    private Score<X> head = null;
    private Score<X> tail = null;

    public LinkedList() {
        this.head = new Score<>(null, null, null);
        this.tail = head;
    }

    public LinkedList<X> add(String input, String input2){
        tail.next = new Score<>(input, input2, null);
        tail = tail.next;
        return this;
    }

    public String print(){
        StringBuffer sb = new StringBuffer();
        for(Score<X> n = head.next; n != null; n = n.next){
            sb.append(n.stdId + ":" + n.score +",");
        }
        return sb.toString();
    }
}

public class Test087 {
    public static void main(String[] args) {

        try(
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        ) {
            String sl = null;
            LinkedList<Score> l = null;
            String str = null;
            while (true) {
                System.out.println("[ M E N U ]");
                System.out.println("1. 새 자료");
                System.out.println("2. 자료 입력");
                System.out.println("3. 파일로 저장");
                System.out.println("4. 파일에서 불러오기");

                sl = br.readLine();
                switch (sl) {
                    case "1":
                        l = new LinkedList<Score>();
                        break;
                    case "2":
                        System.out.println("학번, 점수를 입력하세요.");
                        sl = br.readLine();
                        l.add(sl.split(",")[0],sl.split(",")[1]);
                        break;

                    case "3":
                        System.out.println("case 3");
                        str = l.print();
                        sl = br.readLine();

                        String title = sl;

                        BufferedWriter save = new BufferedWriter(new FileWriter(title+".txt"));
                        save.write(str);
                        save.close();

                        break;
                    case "4":
                        System.out.println("case 4");
                        sl = br.readLine();
                        String title2 = sl;
                        BufferedReader load = new BufferedReader(new FileReader(title2+".txt"));
                        String[] loadStr = load.readLine().split(",");
                        l = new LinkedList<Score>();
                        for(int i = 0; i<loadStr.length; i++ ){
                            l.add(loadStr[i].split(":")[0],loadStr[i].split(":")[1]);
                        }
                        load.close();
                    case "quit":
                        break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

```

### 서버에서 mp3 받기

```java
package socket;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Sever {
    public static void main(String[] args) {

        System.out.println("Before accept()");
        try(
                ServerSocket svr = new ServerSocket(1123);
                Socket skt = svr.accept();
                ObjectInputStream ois = new ObjectInputStream(skt.getInputStream());
                OutputStream os = skt.getOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(os);
                ){

            System.out.println("After accept()");
            String title = ois.readUTF();
            File f = new File(title);
            boolean b = f.exists();
            System.out.println(b);
            if(b){
                oos.writeInt(200);
                oos.flush();

                InputStream file = new BufferedInputStream(new FileInputStream(title));
                int r = 0;
                byte[] buf = new byte[1024*8];
                while((r =file.read(buf)) != -1){
                    System.out.println("파일 전송 준비");
                    os.write(buf, 0 ,r);
                    os.flush();
                }
                file.close();
                //파일이 길이를 리턴한다. (long 형 자료에 주의)
                System.out.println(f.length());
            }else{
                oos.writeInt(404);
                oos.flush();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

```

```java
package socket;

import java.io.*;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        try(
                Socket skt = new Socket("127.0.0.1", 1123);
                ObjectOutputStream oos = new ObjectOutputStream(skt.getOutputStream());
                InputStream is = skt.getInputStream();
                ObjectInputStream ois = new ObjectInputStream(is);
        ){
            oos.writeUTF("music.mp3");
            oos.flush();
            int status = ois.readInt();
            System.out.println(status);
            if(status == 200){
                System.out.println("파일 받기");
                OutputStream file = new BufferedOutputStream(new FileOutputStream("music2.mp3"));
                int r = 0;
                byte[] buf = new byte[1024*8];
                while((r = is.read(buf)) != -1){
                    System.out.println("파일 수신 준비");
                    file.write(buf, 0, r);
                }
            }else{

            }
            ;
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

```

# Day2

---

```java
package javaclass;

public class Test092 {
    public static void main(String[] args) {
        String l = "HelloWorld";
        String r = "HelloWorld";
        String t = l.substring( 2, 5);  //""들어간거는 pool에 들어가지만 ""로안들어간다. 포인터 비교가 가능
        // 그 외의 경우 (substirng 등등)은 Pool 을 쓴다는 보장이 없다
        // - 그 때는 equals로 비교한다.

        System.out.println( r == l);
        System.out.println(  "llo" == t);
        System.out.println( "llo".equals(t));

        //문자열을 숫자로 변환시에 : Double.parseDouble 이용
        int r2 = Integer.parseInt("100");
        System.out.println( r2 == 100);

        
        //메모리를 적게 먹음 , 하나있는 경우에는 이방법이 낫다.
        String tl =  "10101,100";
        int idx = tl.indexOf(",");
        System.out.println(tl.substring(tl.indexOf(",")));
        System.out.println(tl.substring(0, 5));//5위치부터 끝까지
        System.out.println(tl.substring(6));
                
        
        //메모리를 많이먹음, 가 여러개있는 경우에는 이방법이 편하다.
        String[] ls = tl.split(",");
        System.out.println(ls[0] + " " + ls[1]);
    }
}

```

## Mysql

```mysql
show database;
```

> 데이터베이스 보여줌

```mysql
create database study;
```

> 데db 만듬

```mysql
use study;
```

> 만든데이터 베이스로

```mysql
create table study01t(
	id int       not null,
    score tinyint not null
);
```

> not null 옵션의 id와 score 컬럼이있는 테이블 생성

```mysql
drop talble study01t
```

> 테이블 삭제

```mysql
insert into study01t values (10101, 100);
insert into study01t values (10102, 90);
```

> 테이블에 값 추가

```mysql
SELECT * FROM study.study01t;
```

> 테이블 조회

```mysql
insert into study01t (score, id) values  (90, 10104);
```

	> 테이블에 컬럼값으로 삽입

테이블에 있어야 하는 개념



1. 필드(Field) - 컬럼  : 자료형을 지정한다 (int, tinyint), 같은자료형의, 같은 의미의 값이 와야한다.

2. 레코드(Record) - Row : 입력의 단위  - 데이터들이 연관되어지는 묶음이어야 한다.

   이 두가지 개념이 있어야 테이블이라 할 수 있다.

```mysql
delete from study01t where id = 10101;
```

> where 값이 10101;인 레코드 지음

```mysql
delete from study01t where id < 10109;
```

> ​	where 조건을 만족하는 값들 전부지움

```mysql
delete from study01t;
```

> 전부지움

```mysql
delete from stud01t where 0 = 1;
```

> 에러 안남 - 그리고 지우지도않음

```java
delete from stud01t where 1 = 1;
```

> 전부 지움

tinyint : 1byte

int : 4byte

```mysql
update study01t set score = 110 where id = 10101;
```

> where 조건을 만족하는것에대하여 set을 동작

```mysql
update study01t set score = socre-10 where id = 10101;
```

​	> 기존의 값을 활용해서 만들 수 있다.

```mysql
update study01t set score = id - 10000 where id = 10101;
```

```mysql
update study01t set score = id - 10000 where id != 10101;
```

> 같은 레코드의 값을 이용해서 사용하게됨

```mysql
update study01t set id = 0, score = 0 where id = 10101;
```

> 2개이상의 Data를 동시에 수정

where 문장 delete, update, select와 연동

```mysql
select * from study01t where id != 10102;
```

> 10102가아니년석 보여줘

```mysql
select id from study01t where id != 10102;
```

> 특정 필드만 보여줄때

```java
select score, id from study01t where id != 10102;
```

> 스코어 아이디 컬럼순서대로 보여줘

```mysql
select score + 5, id from study01t where id != 10102;
```

> select는 데이터를 가옹해서 보여 줄 수 있다.

```mysql
SELECT score + 5 as sungjuk, id FROM study01t;
```

> 컬럼명을 바꾸어서 출력이 가능하다. (보여지는거만 바꾸었다. 이름바뀐거 아님)

```mysql
select score, score as sungjuk from study01t;
```

> 하나의 컬럼을 여러변 출력해도 무방하다.

### 실습준비

```mysql
CREATE TABLE StudentT(
	stId CHAR(5),
	name VARCHAR(9),
	addr VARCHAR(9)
);

INSERT INTO StudentT VALUES('10101','홍길동','역삼동');
INSERT INTO StudentT VALUES('10102','고길동','개포동');
INSERT INTO StudentT VALUES('10103','이기자','역삼동');
INSERT INTO StudentT VALUES('10104','박기자','한남동');
INSERT INTO StudentT VALUES('10105','김영삼','홍제동');
INSERT INTO StudentT VALUES('10106','김대중','한남동');

CREATE TABLE SubjectT(
	subId   CHAR(4), 
	name   VARCHAR(9) 
);

INSERT INTO SubjectT VALUES ('KOR1','국어1');
INSERT INTO SubjectT VALUES ('ENG1','영어1');
INSERT INTO SubjectT VALUES ('MAT1','수학1');

CREATE TABLE ScoreT(
	stId  CHAR(5),
	subId CHAR(4), 
	score TINYINT
);

INSERT INTO ScoreT VALUES('10101','KOR1',60);
INSERT INTO ScoreT VALUES('10101','ENG1',80);
INSERT INTO ScoreT VALUES('10101','MAT1',90);

INSERT INTO ScoreT VALUES('10102','KOR1',90);
INSERT INTO ScoreT VALUES('10102','MAT1',90);
INSERT INTO ScoreT VALUES('10102','ENG1',100);

INSERT INTO ScoreT VALUES('10103','KOR1',70);
INSERT INTO ScoreT VALUES('10104','KOR1',80);
INSERT INTO ScoreT VALUES('10105','KOR1',50);
INSERT INTO ScoreT VALUES('10106','KOR1',60);

INSERT INTO ScoreT VALUES('10103','ENG1',90);
INSERT INTO ScoreT VALUES('10104','ENG1',70);
INSERT INTO ScoreT VALUES('10105','ENG1',60);
INSERT INTO ScoreT VALUES('10106','ENG1',80);

INSERT INTO ScoreT VALUES('10103','MAT1',70);
INSERT INTO ScoreT VALUES('10104','MAT1',70);
INSERT INTO ScoreT VALUES('10105','MAT1',80);
INSERT INTO ScoreT VALUES('10106','MAT1',60);

CREATE TABLE Score2T (
	stId CHAR(5),
	name VARCHAR(9),
	addr VARCHAR(9),
	kor1 TINYINT,
	eng1 TINYINT,
	mat1 TINYINT
);

INSERT INTO Score2T VALUES('10101','홍길동','역삼동',60,80,90);
INSERT INTO Score2T VALUES('10102','고길동','개포동',90,90,100);
INSERT INTO Score2T VALUES('10103','이기자','역삼동',70,90,70);
INSERT INTO Score2T VALUES('10104','박기자','한남동',80,70,70);
INSERT INTO Score2T VALUES('10105','김영삼','홍제동',50,60,80);
INSERT INTO Score2T VALUES('10106','김대중','한남동',60,80,60);
```

```mysql
create table study02t(
id char(5) not null,
name varchar(10) null
);

insert into study02t values('a0001', 'abcd');
insert into study02t values('a0001', 'abcddasdskdsajdk'); --너무 길어서 에러남
insert into study02t values('a00', 'apple');
```

문자열 : ''로 감싼다. char or varchar 자료형

char : 고정길이 문자열 `char(5)` (5글자만 들어감) (학번 주민번호 ...) (빈자리는 스페이스 채움) 3자리써도 5자리씀

varchar: 가변길이 문자열 varchar(10) (10자리까지 들어간다.) 3자리쓰면 3자리씀

둘다 최대길이를 넘게 쓸 수 없다.

char가 처리속도가 훨씬 빠름



### concat

```mysql
select concat(id, '*') from study02t;
```

```
mysql > a0001*
oracle > a01    *      --'a01   '  'a0001' 'a0001 ' char(5)형태시 이렇게 고정길이
```

> mysql 문자열 붙이기



### select

```mysql
SELECT * FROM study.studentt where addr = '역삼동';
```

> 역삼동에 사는 학생 출력

```mysql
SELECT * FROM study.studentt where addr like '역%';
```

> 역으로시작하는 문자로 사는 학생출력

```mysql
SELECT * FROM study.studentt where addr like '%삼동';
```

> 삼동으로 끝나는것

```mysql
SELECT * FROM study.studentt where addr like '%포%';
```

> 포가 들어간 동에 사는 친구

### substirng

```mysql
SELECT substr(addr, 1, 2) FROM study.studentt
```

> 부분문자열 추출

```mysql
SELECT * FROM study.studentt where substr(addr, 1,2) = '역삼';
```

```mysql
select length(addr) from studentt;
```

```
> 9
```

글자 길이. utf-8인 경우는 한글은 3바이트

## aggregate functions : 5가지

==min	max	count	avg	sum== 외워두자

유일한 결과를 출력하는 성격이 있다.

```mysql
select min(score) from scoret where subid = 'KOR1';
```

```
> 50
```

> 50이 여러개라도 50이 하나

```mysql
select avg(score) from scoret where stId = '10101';
```

> 10101 학생의 평균 성적

```mysql
select * from scoret where subId = 'MAT1' and (score=60 or score=80);
```

> 수학에서 60, 80점 받은 학새은 ( or, 괄호)   +++ 필드명 윈도우에서는 대소문자 안가림 (리눅스에서는 가림)

```mysql
select * from scoret where subid = 'MAT1' and score in (60, 80);
```

> 이렇게써도됨

```mysql
select count(*) from scoret where subid = 'MAT1' and score in (60, 80);
```

> 결과 레코드의 개수를 count(*)로 알 수 있다.

studentt, subjectt, scoret는 얽혀있다.

'여러개의 테이블이 연관관계를 가지고 데이터 베이스를 구성'

```mysql
select * from studentt where addr like '%역삼%';
select * from scoret where subid ='KOR1' and stid in('10101','10103');
select * from scoret where subid ='KOR1' and stid in(select * from studentt where addr like '%역삼%');
```

여러개 나오는 결과를 이용할 때는 IN 또는  NOT IN을 사용한다.

하나의 쿼리의 결과를 이용해 다음쿼리를 돌리는걸 sub query라고 한다.

```mysql
select  avg(score) from scoret where subid ='KOR1';
select * from scoret where subid='KOR1' and score <= (select avg(score) from scoret where subid ='KOR1');
```

평균점수이하의 점수를 받은 사람들출력

유일한 서브쿼리 결과와의 비교는 = != < <= > >= 을 쓴다.

서브쿼리는 반드시 괄호로 묶어주어야 한다.

```mysql
select stid from studentt where name like '김%';
select avg(score) from scoret where subid='MAT1' and stid in(select stid from studentt where name like '김%');
```

> 김씨성을가진 학생들의 수학성적평균

```mysql
update scoret set score = score-5 where subid = 'ENG1' and  stid in (select stid from scoret where subid ='ENG1' and score<=70);
```

> 에러남 아래처럼 짜줘야함 왜냐하면

```mysql
update scoret set score = score-5 where subid = 'ENG1' and  stid in (select * from(select stid from scoret where subid ='ENG1' and score<=70) as X);
```

>  mysql 에서 자기자신을 select하면서 update하는것은 안되기 떄문에 이렇게 해줘야함 oracle은 됨

"AGGREGATE FUCTION은 GROUP BY, HAVING과 연동된다."

```mysql
select stid, avg(score) from scoret group by stid;
```

| 10101 | 76.6667 |
| ----- | ------- |
| 10102 | 93.3333 |
| 10103 | 76.6667 |
| 10104 | 73.3333 |
| 10105 | 63.3333 |
| 10106 | 66.6667 |

stid에 동일한 값을 가진 레코드를 짜매어서 평균낸 결과.

group by를 썼을때는 group by에 지정된 컬럼만 select 절에 와야한다.



평균 처리 75이하의 학생

```mysql
select stid, avg(score) from scoret group by stid where avg(score) <= 75;
```

이걸로 될거같지만 avg보다 where가 먼저 실행되므로 에러가남

where은 Aggregate function보다 먼저 수행됨

때문에 having 은 통계 처리 이후에 동작한다. 때문에 가능

```mysql
select stid, avg(score) from scoret  group by stid having avg(score) <= 75;
```

서브쿼리는 크게 3종류로 나뉜다.

1. Where절의 서브쿼리
2. from 절의 서브쿼리( inline view)
3. select절의 서브쿼리(엄청난 결과 & 엄청난 부담)

### from 절의 서브쿼리

select 결과를 마치 table 처럼 보면된다.

```mysql
select stid, round(avg(score), 2) as xx from scoret  group by stid;
```

`round(avg(score), 2)` 소수점 3번째자리에서 반올림

from절의 서브쿼리에서

```mysql
select * from (select stid, round(avg(score), 2) from scoret  group by stid) as xx where xx <= 75;
```

> 될거 같은데 안된다. 데이터 베이스마다 지원안하기도한다 (오라클에서는 됨)
>
> 원래 이런것들은 view라는것을 만들어서 쓰는게 정석이기 때문

```mysql
select * from (select stid, round(avg(score), 2) as avg from scoret group by stid) xx where avg <= 75;
```

> 하지만 평균에 alias를 설정하면 가능

```mysql
create view score2v as select stid, round(avg(score), 2) as xx from scoret  group by stid;
select * from score2v;
```

| 10101 | 76.67 |
| ----- | ----- |
| 10102 | 93.33 |
| 10103 | 76.67 |
| 10104 | 73.33 |
| 10105 | 63.33 |
| 10106 | 66.67 |

view는 테이블은 아니고 결과를 합해서 볼수있는 하나의 창

-> 보여지기만 하는 화면이므로 수정이나 삭제는 무의미하다.

### select절의 서브쿼리

```mysql
select stid, name, (select avg(score) from scoret) as avg from studentt;
```

| 10101 | 홍길동 | 75.0000 |
| ----- | ------ | ------- |
| 10102 | 고길동 | 75.0000 |
| 10103 | 이기자 | 75.0000 |
| 10104 | 박기자 | 75.0000 |
| 10105 | 김영삼 | 75.0000 |
| 10106 | 김대중 | 75.0000 |

레코드 하나마다 서브쿼리 문장도 돌아간다.

````mysql
select stid, name, (select avg(score) from scoret where stid= '10101') as avg from studentt;
````

| 10101 | 홍길동 | 76.6667 |
| ----- | ------ | ------- |
| 10102 | 고길동 | 76.6667 |
| 10103 | 이기자 | 76.6667 |
| 10104 | 박기자 | 76.6667 |
| 10105 | 김영삼 | 76.6667 |
| 10106 | 김대중 | 76.6667 |

10101의 평균을 구하니 76점으로 전부나오게됨

원래는 `select studentt.stid, studentt.name from studentt;` (우리는 생략)

```mysql
select x.stid, x.name from studentt as x;
```

```mysql
select x.stid, x.name, (select avg(score) from scoret where stid= '10101') as avg from studentt as x;
```

| 10101 | 홍길동 | 76.6667 |
| ----- | ------ | ------- |
| 10102 | 고길동 | 76.6667 |
| 10103 | 이기자 | 76.6667 |
| 10104 | 박기자 | 76.6667 |
| 10105 | 김영삼 | 76.6667 |
| 10106 | 김대중 | 76.6667 |

```mysql
select x.stid, x.name, (select avg(score) from scoret where stid = x.stid) as avg from studentt as x;
```

| 10101 | 홍길동 | 76.6667 |
| ----- | ------ | ------- |
| 10102 | 고길동 | 93.3333 |
| 10103 | 이기자 | 76.6667 |
| 10104 | 박기자 | 73.3333 |
| 10105 | 김영삼 | 63.3333 |
| 10106 | 김대중 | 66.6667 |

alias 별명을 지정해줌으로써 각각의 평균점수를 구할 수 있다.

```mysql
create table temp01t as select stid, avg(score) as avg from scoret group by stid;
```

```mysql
create view temp01v as select stid, avg(score) as avg from scoret group by stid;
```

==결과를 view로 가지고 셀렉트를하면 오버헤드가 많이들어감 생성이후에 데이터 반영 됨==

//점수를 바꾸면 view는 값이 변함

==결과를 table로 가지고 셀렉트를하면 오버헤드 많이안들어감 속도 더빠름 생성이후에 데이터는 반영 x==

// 그때의 결과의 복사품이라 값이안변함

### 등수구하기

```mysql
create table temp02t as  select x.stid , x.avg, ( select count(*)+1 from temp01t where avg> x.avg)as ran from temp01t as x;
```

임시테이블 만들면 오버헤드가 적게발생함. 근데 바뀐점수가 바로 바로 적용되지않음

이래서 새로 수정된 테이블들의 오버헤드를 적게만들기위해 수정된 점수는 몇일 뒤에 공개됩니다 라고 하게되는거

///// 추가 rank는 예약어로 되서 workbench에서 돌렸을때는 에러가 난다. 근데 cmd에서 돌리면 됨

```mysql
select * from temp02t order by ran asc;
```

| 10102 | 93.3333 | 1    |
| ----- | ------- | ---- |
| 10101 | 76.6667 | 2    |
| 10103 | 76.6667 | 2    |
| 10104 | 73.3333 | 4    |
| 10106 | 66.6667 | 5    |
| 10105 | 63.3333 | 6    |

asc는 오름차순 생략가능  desc로 주면 내림차순

- 임시테이블과 뷰는 흩어진 데이터에서 자신이 원하는 데이터로 가공 할 수 있는 방법을 제공한다.

  (비정형 데이터에서 정형화된 데이터를 만들어낸다)

- 뷰는 오버헤드가 있지만 데이터의 변경을 즉각 반영한다. 임시테이블은 오버헤드 적지만 데이터의 변경을 즉각 반영 못함

- select 서브쿼리는 오버헤드가 크다.

  (1000명의 등수를 처리한 결과를 1000명이 동시 열람하면 100만건의 쿼리가 동작하는셈 + group by 오버헤드 포함)

- 임시테이블은 이러한 부담을 극적으로 줄여준다

   (도사들은 이걸 기가막히게 잘 쓴다.)

```mysql
create  table study3t study02t(
    no int not null auto_increment primary key,
theTime datetime not null);
```

```mysql
insert into study3t values (default, now());
```

```mysql
SELECT * FROM study.study02t;
```

| a0001 | abcd       |
| ----- | ---------- |
| a0001 | helloworld |
| a00   | apple      |

mysql은 일련번호 auto_increment privmary key 사용  오라클은 sequence 이용

now()는 현재시간, 그것을 입력할때 datatime 자료형을 쓴다.

```mysql
select no, date_add(theTime, INTERVAL 1 MONTH) from study3t;
select no, date_add(theTime, INTERVAL 4 DAY) from study3t;
select no, date_add(theTime, INTERVAL 1 HOUR) from study3t;
```

시간이 추가되는 연산

datetime 자료형에 들어있는 값은 연산이 가능하다

==연산을 해야 할필요가 없을떄는 char를 쓰는게 바람직== 오버헤드가 더 적기 때문

```mysql
create  table study4t(
    no int not null auto_increment primary key,
theTime char(19) not null);
```

```mysql
insert into study4t values (default, now());
```

| 1    | 2019-07-23 17:25:28 |
| ---- | ------------------- |
| 2    | 2019-07-23 17:25:29 |
| 3    | 2019-07-23 17:25:29 |

char로 선언해서 연산은 불가능하지만 오버헤드가 적음

# Day3

---

## 데이터 설계 프로세스

### 업무분석

- 명사, 동사에 주의하여 업무를 정확하게 기술한다.

- 업무 파악에 도움이 되는 어떤 형태의 자료도 확보한다.

- 가능한 UI를 그려가면서 인터뷰를 진행한다.

  (나중에 갈아업더라도 이 때 그려진 UI는 설계에 결정적인 영향을 끼친다.)

- 말 만들기에 떄라서 설계에 영향이 갈 수있다. 경험이 중요하다.

### 엔티티(Entity) 도출

+ 추상명사 중에서 PK로 구분되어지는 것이 Entity가 된다.

+ PK는 단일 필드의 특징이 있어야 한다.

  (주민 번호의 경우 두개의  필드로 볼 수 있지만 단일한 성격으로 파악하는 것이 바람직하다.)

+ 기록될 만한 가치가 있는 정보인지 판별해야 한다.

PK (Primary Key) (기본키라곧 부르기도 한다)

: 레코드를 구분하는 기준으로 사용되는 필드들

ex) 주민번호, 사번, 군번 ...

주민번호처럼 두개의 필드가 결합하여 PK를 이룰 수 있다.

PK는 3가지의 성격을 가져야한다.

1. ND(No Duplicate): 중복되서는 안된다.

   주민번호 중복되면 큰일남

2. NN(Not Null): 생략되어서는 안된다.

   주민번호 없는 국민??

3. NC(No Change): 가급적 변경되어서는 안된다.

   주민번호 한버바꾸면 모든 기록들에 영향이감

[ 엔티티는 사각형으로 ERD에서 표현된다.]

엔티티 예) 회원, 글, 과목, 학생

### Relation (관계) 도출

- 엔티티들 사이에서 동사가 '어울리게' 존재 가능하고, 그것이 기록할 만한 가치가 있다면 그것이 Relation이다.

예) 회원은 글을 쓴다.(쓴다)

​	  학생은 과목을 수강한다. (수강한다)

1. 일대일 대응 : Be 동시에 해당하는 관계 or 상속관계

   예) 사병은 군인이다. 장교는 군인이다. 군인은 군번으로 구분된다.

2. 일대 다 대응 : [설명 참고]

   예) 회원은 글을 쓴다.

3. 다대다 대응 : [설명 참고]

   학생은 과목을 수강한다. 회원은 글을 읽는다. 또한 추천한다.

관계의 물리적인 구현방법

1. 일대일 대응 : 조상의 PK를 자손의 PK이자 FK로 참조한다.
2. 일대다 대응 : '다' 쪽에서 '일' 쪽의 PK를 FK로 참조한다.
3. 다대다 대응 : 새로운 테이블을 만들고, 그 PK는 양쪽의 PK를 참조하는 FK를 결합하여 구성한다.

### Attribute (속성) 파악

- 일반명사중에서 자료형으로 값으로 표현 될 수 있는 것들.

  예) 성적, 글쓴시간, 이름, 전화번호, 주소 ...

- Entity, Relation에 1:1 로 매핑되는 곳에 배치한다.

- 실제 구현시 필드에 해당한다.

### ERD 구성

- 분석에 가까운 형태, 구현에 가까운 형태 두가지가있다.

  (두가지 다 그릴 줄 알아야한다.)

- 엔티티는 사각형, 관계는 마름모로 그린다.

  관계는 화살표 또는 실선으로 그린다. (도착쪽이 PK)

  PK는 꽉찬 사탕막대기, 그 외 필드는 텅 빈 사탕막대기.

  NOT NULL 필드는 굵은 글씨로 표기한다.

- 그릴떄 용이한 많은 툴이 있다.

  Microsoft Visio 를 강사 개인적으로는 추천한다.

....

정규화  : More Table, Less Column

비정규화 : Less Table, More Column

- 비정규화는 속도는 빠르지만 테이블 구조가 자주 변경될 여지가 있다.
- 정규화는 속도는 느릴 수 있지만 테이블 구조가 안정적이다.
- 정규화는 자료의 중복저장을 허용 안한다.(성적만 있으면 등수는 자동)
- 비정규화는 자료의 중복저장을 허용하는 경향 ( 속도를 위해 등수필요)

"결국 정규화를 중심으로 해서 적절한 비정규화를 추구하는 게 방향이지만 정답은 없다. 해서 이 분야눈 경험이 절대적으로 중요하다." 

![boardERD](..\images\boardERD.png)

> 게시판의 개념 관계행 데이터 베이스 모델

**Foreign Key : 외래키**

다른테이블의 PK로 쓰이는 필드를 내 쪽에서 참조해서 쓰는 필드(들)

성적 테이블의 stid, subid - 성적 테이블의 stid는 학생테이블에 쓰이는 의미를 가져다가 쓴다.

(성적테이블의 stid의 10101dms gkrtodxpdlqmfmdl stid의 10101과 동일한 의미이다.)

## 정규화

```mysql
select * from score2t;
select stid,name,(kor1 + eng1 +mat1)/3 as avg from score2t;
```

| 10101 | 홍길동 | 76.6667 |
| ----- | ------ | ------- |
| 10102 | 고길동 | 93.3333 |
| 10103 | 이기자 | 76.6667 |
| 10104 | 박기자 | 73.3333 |
| 10105 | 김영삼 | 63.3333 |
| 10106 | 김대중 | 66.6667 |

score2t는 과목이 늘거나 줄 때에 대첵이  심각하다.

그러나 동작속도는 무지하게 빠르다.

score2t와 같이 설계된 경우를 비정규화 라고한다.

정규화 : Less Column (테이블당 필드의 갯수가 적다 - 5~12개)

More Table (score2t가 한개로 되는 걸 우리는 3개를 만들었다...

필드와 데이터의 중복저장을 허용 안한다.

필드와 데이터의 중복 저장을 허용 안한다

비정규화 : More Column, Less Table

필드와 데이터의 중복 저장을허용한다. (속도 때문에)

"대부분 정규화를 기본으로 해서 적절한 비정규화를 도입한다."

```mysql
create table student_xt as select stid, name, addr from score2t where 0 = 1;
```

> 껍데기만 있는 테이블 복사

```mysql
insert into student_xt select stid, name, addr from score2t where 1=1;
```

> 껍데기에 자료 복붙

## Join(서브쿼리와 유사한데, 서브쿼리와 좀 다르다)

### inner join

studentt : stid (PK) <- scoret : stid(FK)

대부분의 Join은 PK-FK 사이에서 일어난다.

```mysql
Select * from studentt inner join scoret on studentt.stid = scort.stid;
```

mysql  : join  on

oracle : 그냥 where절

```mysql
Select name,addr,score from studentt inner join scoret on studentt.stId = scoret.stId where subid ='MAT1';
```

| 홍길동 | 역삼동 | 90   |
| ------ | ------ | ---- |
| 고길동 | 개포동 | 90   |
| 이기자 | 역삼동 | 70   |
| 박기자 | 한남동 | 70   |
| 김영삼 | 홍제동 | 80   |
| 김대중 | 한남동 | 60   |

Join을 이용하면 흩어진 데이터를 통합해서 보여지게 할 수 있다.

(서브쿼리도 가능, 헌데 성능이 틀리다. 해서 같은 결과를 만들되 성능이 향상되게 만드는것이 SQL 튜닝의 영역)

```mysql
Select stid, name, score,  x.subid from scoret as x inner join subjectt as y on y.subId = x.subId;
```

| 10101 | 국어1 | 60   | KOR1 |
| ----- | ----- | ---- | ---- |
| 10101 | 영어1 | 80   | ENG1 |
| 10101 | 수학1 | 90   | MAT1 |
| 10102 | 국어1 | 90   | KOR1 |
| 10102 | 수학1 | 90   | MAT1 |
| 10102 | 영어1 | 100  | ENG1 |
| 10103 | 국어1 | 70   | KOR1 |
| 10104 | 국어1 | 80   | KOR1 |
| 10105 | 국어1 | 50   | KOR1 |
| 10106 | 국어1 | 60   | KOR1 |
| 10103 | 영어1 | 90   | ENG1 |
| 10104 | 영어1 | 70   | ENG1 |
| 10105 | 영어1 | 60   | ENG1 |
| 10106 | 영어1 | 80   | ENG1 |
| 10103 | 수학1 | 70   | MAT1 |
| 10104 | 수학1 | 70   | MAT1 |
| 10105 | 수학1 | 80   | MAT1 |
| 10106 | 수학1 | 60   | MAT1 |

```mysql
select name, avg, y.stid, addr from (select stid, avg(score) as avg from scoret group by stid) as x inner join studentt as y on y.stId = x.stid;
```

from절의 서브쿼리 결과도 inner join이 가능하더라.

### outer join

```mysql
insert into subjectt values ('PHY1', '물리');
select * from subjectt inner join score on subjectt.subid = scoret.subid;
```

물리가 추가되었어도 한건의 성적 데이터가 없으니 짜매어줄 대상이없다.

회원가입이 되어도 글 쓴게 없으면 조인 걸어도 나타나지않는다.

이런식으로 한쪽 테이블에서만 보여지고 짜매지지 않는 경우라도 한건 보여지게 만드는 형태의 조인이 Outer join 이다.

```mysql
select * from subjectt left outer join scoret on subjectt.subid = scoret.subid;
```

| MAT1 | 수학1 | 10103 | MAT1 | 70   |
| ---- | ----- | ----- | ---- | ---- |
| MAT1 | 수학1 | 10104 | MAT1 | 70   |
| MAT1 | 수학1 | 10105 | MAT1 | 80   |
| MAT1 | 수학1 | 10106 | MAT1 | 60   |
| PHY1 | 물리  |       |      |      |

left : 부족함(null로 채움)이 나타는 반대편을 명시한다. 

subjectt랑 scoret 바꾸면 PHY1값 나타나지않음

```mysql
select subjectt.subid, count(*) from subjectt left outer join scoret on subjectt.subid = scoret.subid group by subjectt.subid;
```

| KOR1 | 6    |
| ---- | ---- |
| ENG1 | 6    |
| MAT1 | 6    |
| PHY1 | 1    |

수강학생들 과목 값 함 출혁했는데 물리는 null인데 값이 들어가있어서 하나 있는걸로 세어짐

떄문에 *을 필드로 명시

```mysql
select subjectt.subid, count(score) from subjectt left outer join scoret on subjectt.subid = scoret.subid group by subjectt.subid;
```

| KOR1 | 6    |
| ---- | ---- |
| ENG1 | 6    |
| MAT1 | 6    |
| PHY1 | 0    |

count(*)은 레코드 갯수를 세고, count(필드)는 해당 필드의 null이 아닌 데이터의 갯수를 센다.

## constraint

물리적인 제약조건을 필드(들) 에 걸어준다.

socre : 0 ~ 100 사이의 값만 허용해야한다.

이런건 아예 못들어가게 막아야 한다.

### check

```mysql
#insert into scoret values ('10101', 'PHY1', 120);
#delete from scoret where score > 100;
alter table scoret add constraint check_scort_score check ( score >= 0 and score <= 100);
```

alter table (테이블 수정) add constraint (제약조건 추가)

제약조건은 check, unique, primary key, foreign key 4가지를 주로씀 

지울때는 

```mysql
alter table scoret drop check check_scort_score;
```

check constraint는 where절의 조건을 이용하여 제약을 걸 수 있다.

(in, not in, =, != ...을 사용할 수 있다.)

### primary key

```mysql
alter table subjectt add constraint PK_subjectt_subid primary key (subid);
```

> 프라이머리 키 지정
>
> not null, no duplicate를 물리적으로 보장
>
> 테이블당 하나의 pk constraint 사용가능

### foreign key

```mysql
alter table scoret add constraint fk_scoret_subid foreign key (subid) references subjectt (subid);
```

> 외래키 지정

```mysql
insert into scoret values ('10101', 'XXXX', 50);  -- 에러
insert into scoret values ('10101', 'PHY1', 50);
```

참조 무결성 : PK쪽에서 쓰여진 데이터만 FK 쪽에서 쓰여질 수 있다.

- 회원가입해야 글 쓴다.
- 회원 등록 해야 예약한다.
- 등록된 아이템만 대여가능하다.

### unique

```mysql
create table study5t(id int not null);
alter table study5t add constraint uq_study5y_id unique (id);
insert into study5t values (100);
insert into study5t values (100);  -- 에러남 같은값 집어놔서
```

다대다로 만들고 -> unique -> 일대다로 동작

일대다 상황에서 테이블 분리하는 방법

1. 일단 다대다로 생각하고 테이블을 생성

2. PK중 하나에 unique constraint를 건다

   다대다가 일대다로 바뀐다

   PK 를 어떻게 할지를 선택하여 결정한다

```mysql
alter table study5t drop index uq_study5y_id;
```

유니크 지울때

자세한 사항은 - https://www.w3schools.com/sql/sql_ref_drop_constraint.asp 참조

## 문제

### score2t로 subject 기반 subject_xt, score_xt를 만들고 기존의 데이터 재배치

```mysql
create table score_xt as select stid, 'KOR1' as subid, kor1 as score from score2t where 0 = 1;
insert into score_xt select stid, 'KOR1', kor1 as score from score2t where 1=1;
insert into score_xt select stid, 'ENG1', eng1 as score from score2t where 1=1;
insert into score_xt select stid, 'MAT1', mat1 as score from score2t where 1=1;
```

| 10101 | 홍길동 | 역삼동 |
| ----- | ------ | ------ |
| 10102 | 고길동 | 개포동 |
| 10103 | 이기자 | 역삼동 |

비정규화된 설계에서 정구화된 설계로 옮길 수도 있고, 정규화된 설계에서 비정규화된 설계로 옮길 수도 있어야 한다.

# Day4

---

단일 thread 서버는 한순간에 접속이 몰리면 과부화되서 멈추는 현상이 발생

- 때문에 개개인의 접근을 쓰레드로 돌릴 필요가 있다.

(가상의 CPU)

```java
class CustomTreaded implements Runnable{
    @Override
    public void run() throws Exception{  <--- 에러남 오버라이딩할떄 thorws Exception못함 조상에 선언된 대로만 재정의해야함        
    }
}
```

```java
package javaclass;

class CustomTreaded implements Runnable{

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            System.out.println("HelloWorld" + i);
        }
    }
}

public class Test093 {
    public static void main(String[] args) {
        Thread thread = new Thread(new CustomTreaded());
        thread.start();
        // new Thread() 하면 가상의 CPU를 OS에서 할당받는다 (분신)
        // 할당받은 CPU는 생성자에 넘겨진 포인터를 물고간다.
        // start() 호출시에 준비과정을 거쳐 새로운 가상 CPU가 rb.run을 호출한다.
        Thread thread1 = new Thread(new CustomTreaded(){
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    System.out.println("HelloWorld" + i);
                }
            }
        });
        thread1.start();
        Thread thread2 = new Thread(()->{
            System.out.println("lamda");
        });
        thread2.start();
    }
}
```

1. Runnable 상속받은 클래스 선언
2. new Thread하면서 1의 인스턴스 포인터를 넘긴다.
3. Thread::start() 호출하면 가상 CPU(Thread)가 run()을 호출

Program : executable file .

Process : a running program

Thread : a light-weighted process . (독자 행동을 하지만 조금 다르다)

:쓰레드는 프로세스 안에서만 존재가가능하다

:쓰레드간 메모리를 공유 할 수 있다.

프로세스간은 메모리 전달은 가능해도 공유는 불가능하다.

프로세스간의 메모리 전달의 대표적 수단이 소켓

(복사 & 붙이기)도 전달로 볼 수 있지만 이건 윈도우에 국한된 개념

프로세스 종료 == 프로세스가 가진 모든 쓰레드의 종료

(인간으로 생각하면 이해 쉬움 : 뇌 위장 척추 ...)

```java
package javaclass;


class A implements Runnable{

    @Override
    public void run() {
        for (int i = 0; i < 100 ; i ++) {
            System.out.println("Apple");
            int time = (int) (Math.random() * 1000);
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class B implements Runnable{

    @Override
    public void run() {
        for (int i = 0; i < 100 ; i ++) {
            System.out.println("Banana");
            int time = (int) (Math.random() * 1000);
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
public class Test094 {
    public static void main(String[] args) {
        new Thread(new A()).start();
        new Thread(new B()).start();
// 쓰레드는 독자적으로 돌아가는 프로그램이된다.
    }
}
```

:Apple 사이에 Banana 끼어들게 안짰는데 결과는 그러했다.

```java
package javaclass;

class A implements Runnable{

    public A(Toilet toilet) {
        this.toilet = toilet;
    }
    private Toilet toilet = null;

    @Override
    public void run() {
        for (int i = 0; i < 100 ; i ++) {
            toilet.bigWork("Apple");
            int time = (int) (Math.random() * 1000);
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
class B implements Runnable{

    private Toilet toilet = null;
    public B(Toilet toilet) {
        this.toilet = toilet;
    }
    @Override
    public void run() {
        for (int i = 0; i < 100 ; i ++) {
            toilet.bigWork("banana");
            int time = (int) (Math.random() * 1000);
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
class Toilet{
    public void bigWork(String str){
        System.out.println("step 1:" + str + " do big work");
        System.out.println("step 2:" + str + " do big work");
        System.out.println("step 3:" + str + " do big work");
        System.out.println("step 4:" + str + " do big work");
        System.out.println("step 5:" + str + " do big work");
    }
}
public class Test094 {
    public static void main(String[] args) {
        Toilet t = new Toilet();
        new Thread(new A(t)).start();
        new Thread(new B(t)).start();
// 쓰레드는 독자적으로 돌아가는 프로그램이된다.
    }
}
```

```
step 4:Apple do big work
step 5:Apple do big work
step 1:banana do big work
step 2:banana do big work
step 3:banana do big work
step 4:banana do big work
step 5:banana do big work
step 1:banana do big work
step 2:banana do big work
```

> 하나의 인스턴스에 같이 접근하다가 대참사가 일어나버림 (하나의 화장실에 둘이 들어가버림;;)
>
> 그래서 문을 잠궈야함!

쓰레드 프로그램에서는 잠금이 중요한데 그것을 동기화 (synchronization) 이라고 한다.

```java
class Toilet{
    public void bigWork(String str){
        synchronized (this) {
            System.out.println("step 1:" + str + " do big work");
            System.out.println("step 2:" + str + " do big work");
            System.out.println("step 3:" + str + " do big work");
            System.out.println("step 4:" + str + " do big work");
            System.out.println("step 5:" + str + " do big work");
        }
    }
}
```

```
step 1:Apple do big work
step 2:Apple do big work
step 3:Apple do big work
step 4:Apple do big work
step 5:Apple do big work
step 1:banana do big work
step 2:banana do big work
step 3:banana do big work
step 4:banana do big work
step 5:banana do big work
```

> 모든 인스턴스에는 lock이라는 개념의 자물쇠/ 열쇠가있다.

this가 가리ㅣ는 인스턴스가 가지고 있는 록을 획득해야 { 에 진입 가능.

획득하지 못하면 쓰레드는 멈추어 기다려야한다.

일을 마쳤으면 } 시점에서 lock을 반납한다.

이런 방법으로 공유하는 메모리에서 작업 도중 끊기는 일을 막을 수 있다.

```java
package javaclass;

class A implements Runnable{
    public A(Toilet toilet) {
        this.toilet = toilet;
    }
    private Toilet toilet = null;
    @Override
    public void run() {
        for (int i = 0; i < 100 ; i ++) {
            int time = (int) (Math.random() * 1000);
            if(time % 2 == 0){
                toilet.sleepWork("Apple");
            }else{
                toilet.bigWork("Apple");
            }
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
class B implements Runnable{

    private Toilet toilet = null;

    public B(Toilet toilet) {
        this.toilet = toilet;
    }
    @Override
    public void run() {
        for (int i = 0; i < 100 ; i ++) {
            int time = (int) (Math.random() * 1000);
            if(time % 2 == 0){
                toilet.sleepWork("Banana");
            }else{
                toilet.bigWork("Banana");
            }
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
class Toilet{
    public void bigWork(String str){
        synchronized (this) {
            System.out.println("step 1:" + str + " do big work");
            System.out.println("step 2:" + str + " do big work");
            System.out.println("step 3:" + str + " do big work");
            System.out.println("step 4:" + str + " do big work");
            System.out.println("step 5:" + str + " do big work");
        }
    }
    public synchronized void sleepWork(String str){
            System.out.println("step 1:" + str + " zzz");
            System.out.println("step 2:" + str + " zzz");
            System.out.println("step 3:" + str + " zzz");
    }
}
public class Test094 {
    public static void main(String[] args) {
        Toilet t = new Toilet();
        new Thread(new A(t)).start();
        new Thread(new B(t)).start();
// 쓰레드는 독자적으로 돌아가는 프로그램이된다.
    }
}
```

```
step 1:Apple do big work
step 2:Apple do big work
step 3:Apple do big work
step 4:Apple do big work
step 5:Apple do big work
step 1:Apple zzz
step 2:Apple zzz
step 3:Apple zzz
```

메서드에도 synchronized 걸 수 있다. Apple의 big work가 호출되고 sleep이 호출될경우 이어서 됨 synchronized를 걸지않으면 쓰레드 중간에 서로 호출이 다를 수 있음

### join

쓰레드를 여러개 생성했는데 최종적으로 그걸 다 기다려야 할 때 join 필요

```java
package javaclass;

class E implements Runnable{
    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            System.out.println("a");
        }
    }
}
class D implements Runnable{
    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            System.out.println("b");
        }
    }
}
class C implements Runnable{
    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            System.out.println("c");
        }
    }
}

public class Test098 {
    public static void main(String[] args) {
        new Thread(new C()).start();
        new Thread(new D()).start();
        new Thread(new E()).start();

        System.out.println("최종정리");
    }
}

```

```
a
a
a
최종정리
a
b
b
b
```

일이 다 끝나기전에 최종정리가 출력됨

```java
public class Test098 {
    public static void main(String[] args) {
        Thread[] threads = new Thread[3];
        threads[0] = new Thread(new C());
        threads[1] = new Thread(new D());
        threads[2] = new Thread(new E());
        threads[0].start();
        threads[1].start();
        threads[2].start();

        try{
            threads[1].join();
        }catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("최종정리");
    }
}
```

D 쓰레드가 끝나기 전까지는 최종정리가 나오지않음

```java
 try{
            threads[0].join();
            threads[1].join();
            threads[2].join();
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
System.out.println("최종정리");
```

세 쓰레드가 끝나기전까지는 최종 정리 문구 나오지않음

### JDBC

```java
package javaclass;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Test099 {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/study?characterEncoding=UTF-8&serverTimezone=UTC", "root", "tara0501");
        Statement stmt = conn.createStatement();
        System.out.println(stmt);
        conn.close();
    }
}//jar파일은 클래스 파일을 압축해서 배포하는 파일
//java -classpath .;mysql-connector-java-8.0.16.jar Test099
```

+ Connection 은 mysql에 소켓으로 접속하는 것과 관계 깊음
+ study : 데이터베이스명
+ root/ tara0501 계정 및 암호
+ 127.0.0.1 : 루프백 아이피

```java

public class Test099 {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/study?characterEncoding=UTF-8&serverTimezone=UTC", "root", "tara0501");
        Statement stmt = conn.createStatement();
        System.out.println(stmt);
        String sql = "insert into studentt values('10107', '또오치', '쌍문동')";
        String sql2 = "delete from studentt where name = '또오치'";
        String sql3 = "update studentt set addr = '이도동' where stid='10101'";
        stmt.executeUpdate(sql3);
        int rc = stmt.executeUpdate(sql2);
        System.out.println(rc);
        stmt.close();
        conn.close();
    }
}
```

Statement는 줄을 타고 오가는 바구니를 연상하면 된다.

1. 커넥션 설정(IP, PORT, ID, PW)
2. 스테이트먼트 설정(SQL, 결과)

excuteUpdate함수의 리턴값은 변경된 레코드의 갯수이다.

🤔🤔🤔🤔🤔🤔🤔🤔🤔🤔🤔🤔🤔🤔🤔🤔🤔🤔🤔

select는 레코드를 변경하지 않는다. 해서 excuteUpdate는 insert / delete / update 문장에 사용한다.

+ conn.close() 신중하게 해야한다. (줄 끊는것)
+ stmt.close()도 신중하게 (바구니 내리는거)
+ conn 형성 - stmt 형성 - 작업 - stmt.close() - conn.close()

이 순서를 지켜서 작업한다.

```java
    static {
//        static initializer는 클래스가 로딩되는 시점에 호출
    }
```

Connection, Statement 모두 인터페이스이다.

DriverManager.getConnection 안에서는 Connection을 상속받은 모종의 클래스의 인스턴스를 리턴한다.

그것은 Mysql에 접속할 수 있는 기능을구현하고 있다.

그 모종의 클래스를 세팅하는 코드가 `Class.forName("com.mysql.jdbc.Driver")`이다.

```java
package javaclass;

import java.sql.*;

public class Test099 {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/study?serverTimezone=UTC", "root", "tara0501");
        System.out.println(conn.getClass().getName());
        Statement stmt = conn.createStatement();
        String sql = "select * from studentt";
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()){
            String stid = rs.getString("stid");
            String name =  rs.getString("name");
            String addr = rs.getString("addr");
            System.out.println(stid + "\t" + name + "\t" + addr);
        }
        rs.close();
        stmt.close();
        conn.close();
    }
}
```

셀렉트를 한결과는 서버쪽에 저장된다. 자바는 포인터로 결과를 가르킬 뿐 connection을 close() 해보면 이를 알  수 있다.

이를 serverside cursor 라고 한다.

rs.next() 하면 리턴된 Result 셋에서 한 레코드씩 아래로 내려감 있으면 true 없으면 false

`ResultSet`은 cursor (slect 결과)에 접근 가능한 정보.

cursor는 서버에 생긴다.

Connection이 닫힘 다음에서는 ResultSet은 사용 불가하다.

(Connection 닫기 전에 사용 끝나야 한다.)

Connection은 대단히 비싼자원이고 제한적이다.

접속후에 빨리 끊어주는게 바람직하다 ( 콜센터를 연상하면 된다.)	

```java
package javaclass;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class StudentVO {
    //property는 멤버변수를이야기함
    //헌데 멤버 변수는 getter/setter 를 이용하고 private하게 선언.
    private String stId = null;
    private String name = null;

    public String getStId() {
        return stId;
    }

    public void setStId(String stId) {
        this.stId = stId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    private String addr = null;
}


public class Test099 {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        // O-R 규칙 (Golden Rule, Rule of Thumb)
        //field -> property
        //table -> class
        //record -> instance
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/study?serverTimezone=UTC", "root", "tara0501");
        System.out.println(conn.getClass().getName());
        Statement stmt = conn.createStatement();
        String sql = "select stid, addr, name from studentt"; //*은 인젝션에 취약함
        ResultSet rs = stmt.executeQuery(sql);

        List<StudentVO> rl = new ArrayList<>();//쌓기만하니 array리스트가 속도가 빠르니 유리
        while (rs.next()){// Connection은 살아있을때 할 거 다해야 한다. Connection은 빨리 끊어야 한다.
            StudentVO vo = new StudentVO();
            vo.setStId(rs.getString("stid"));
            vo.setAddr(rs.getString("addr"));
            vo.setName( rs.getString("name"));
            rl.add(vo);
        }
        rs.close();
        stmt.close();
        conn.close();
        //close 이후에도 list 안에는 결과가 남아있게 됨
        for(StudentVO vo : rl){
            System.out.println(vo.getStId() + "\t" + vo.getName() + "\t" + vo.getAddr());
        }
    }
}
```

VO  : ValueObject 의 약자 - 값을 담는 객체

DTO : Data Transfer Object

Entity 등을 사용하는 경우도 있는데 다 같은 이야기

## 부록

OBS 녹화 프로그램 설치하고 NVIDIA 제어판에서 프로그램설정에서 OBS 프로그램을 통합그래픽으로 지정해줘야 녹화됨

## 문제

```java
package socket;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Sever {
    public static void main(String[] args) throws IOException {
        ServerSocket svr = new ServerSocket(1123);
        for(int i = 0; i < 3; i ++) {
            System.out.println("Before accept()");
            try (
                    Socket skt = svr.accept();
                    ObjectInputStream ois = new ObjectInputStream(skt.getInputStream());
                    OutputStream os = skt.getOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(os);
            ) {
                System.out.println("After accept()");
                String title = ois.readUTF();
                File f = new File(title);
                boolean b = f.exists();
                System.out.println(b);
                if (b) {
                    oos.writeInt(200);
                    oos.flush();
                    Thread thread = new Thread(new FileDownThread(os, title, skt));
                    thread.start();
                    thread.join();
                    //파일이 길이를 리턴한다. (long 형 자료에 주의)
                    System.out.println(f.length());
                } else {
                    oos.writeInt(404);
                    oos.flush();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        svr.close();
        System.out.println("서버 종료");
    }
}
```

```java
package socket;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class FileDownThread implements Runnable{
    private OutputStream os;
    private String title;
    private Socket socket;

    public FileDownThread(OutputStream os, String title, Socket skt) {
        this.os = os;
        this.title = title;
        this.socket = skt;
    }

    @Override
    public void run() {
        try(
        InputStream file = new BufferedInputStream(new FileInputStream(title));) {
            int r = 0;
            byte[] buf = new byte[512];
            while ((r = file.read(buf)) != -1) {
                System.out.println("--- Down loading ---");
                os.write(buf, 0, r);
                os.flush();
            }
            socket.close();
        }catch(Exception e){

        }

    }
}
```

```java
package socket;

import java.io.*;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        try(
                Socket skt = new Socket("127.0.0.1", 1123);
                ObjectOutputStream oos = new ObjectOutputStream(skt.getOutputStream());
                InputStream is = skt.getInputStream();
                ObjectInputStream ois = new ObjectInputStream(is);

        ){
            oos.writeUTF("music.mp3");
            oos.flush();
            int status = ois.readInt();
            System.out.println(status);
            if(status == 200){
                System.out.println("파일 받기");
                int rand = (int) (Math.random()* 1000);
                OutputStream file = new BufferedOutputStream(new FileOutputStream("music"+rand+".mp3"));
                int r = 0;
                byte[] buf = new byte[512];
                while((r = is.read(buf)) != -1){
                    System.out.println("파일 수신 준비");
                    file.write(buf,0,r);
                }
                file.close();
            }else{

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
```

# Day5

---

짜장면집 ERD

![shopERD](..\images\shopERD.png)

```
-- USER SQL
CREATE USER "C##TEST" IDENTIFIED BY "tara0501"  ;

-- QUOTAS

-- ROLES
GRANT "CONNECT" TO "C##TEST" ;
GRANT "RESOURCE" TO "C##TEST" ;

```

create table study10t(

id NUMBER(3),

data VARCHAR2(10));



INSERT INTO study

오라클 숫자 : NUMBER(3) -최대 세자리수 숫자

VARCHAR2 - 오라클에서 만든 속도가 조금 빠른 VARCHAR



--

create table study11t(

id NUMBER(5),

data CHAR(10));

-

-- 오라클과 mysql은 일련번호 만드는 법이 틀리다.

:mysql auto_increment primary key 를 썻다.

create sequence seq_study11;

insert into study11t values (seq_study11.NEXTVAL, 'apple');



select id, data || '*' from study11t;

--mysql의 concat과 동일한 기능을 수행한다.

--char(10)으로 선언한 필드에 'apple'을 넣으면 'apple     '이 된다. 고정길이

Trim()- 좌우의 공백문자를 제거하는 역할을 한다.

select id, TRIM(data) || '*' from study11t;

create table study12t(

the_time date);



insert into study12t values(sysdate);

select to_char(the_time, 'YYYY-MM-DD') from study12t;

-오라클으 ㅣ날짜시간은 date 자료형을 이용한다 현재 시간은 sysdate를 이용한다.

보여지는 형식은 to_char을 이용하여 형식을 지정하면 된다.

select to_char(the_time, 'YYYY-MM-DD HH24:MI:SS') from study12t;



역삼동에 사는 학생의 국어성적을 서브쿼리로 구하세요.

select stid from studentt where addr like '%역삼%';

select * from scoret where stid in ('10101', '10103') and subid = 'KOR1';

select * from scoret where stid in (select stid from studentt where addr like '%역삼%') and subid = 'KOR1'



학생별 평균점수를 group by로 구하세요

select stid, avg(score) from scoret group by stid;

--

select * from studentt inner join scoret on studentt.stid = scoret.stid;

오라클은 이렇게 이너조인을 할 수 있다.

select * from studentt, scoret where studentt.stid = scoret.stid;

...

insert into subjectt values ('PHY1', '물리');

select * from subjectt LEFT OUTER JOIN scoret ON subjectt.subid = scoret.subid;



--오라클용 아우터 조인의 문법

null값으로 채워지는 일이 발생하는 쪽에 (+) 표시를 붙인다.

select * from subjectt, scoret WHERE subjectt.subid = scoret.subid(+);



-- inner join on, outer join on 국제표준 SQL

각 DB 별로 변형 SQL을 탑재

오라클의 변형방법을 다른 DB업체들이 따라하기도 한다.

오라클만 쓰는 사람들은 오라클의 방법만을 고집하는 경우가 많다.

mysql시 테이블의 별칭을 줄 떄는 as 사용 x (mysql은 선택)

select * from (select stid, avg(score) as avg from scoret group by stid) x;



select * from (select stid, avg(score) as avg from scoret group by stid) x, studentt y where x.stid = y.stid;

-- as문법, join의 문법이 약간 틀리다. 하지만 기본 개념은 동일하다.

거의 모든 데이터베이스의 기본 개념은 같다

하나 해놓으면 다른거 어렵지 않다.

Constraint in Oracle

Primarykey, foreign key, check, unique, not null

alter table studentt add constraint pk_studentt_stid primary key (stid);

참조 무결성 :FK쪽에는 PK에 없는 데이터는 존재하면 안된다.

alter table studentt add constraint fk_scoret_stid foreign key (stid) references studentt (stid);

--

delete from studentt where stid = '10101'

primary key에서 지워지면 참조무결성 위배됨 foreign key에서 없는 primary key를 참조하게 되기 떄문

insert into scoret values('10109', 'KOR1', 100); -> 10109라는 사람이 없으니 참조무결성위배해서 실행안됨



alter tavle scoret add constraint fk_scoret_subid foreign key (subid) references subjectt (subid);-안만들어짐

fk constraint는 먼저 참조할 대상 pk constraint가 존재해야 생성 가능

alter table scoret add constraint ck_scoret_score check(score >=0 and score <= 100);



insert into scoret values ('10101', 'PHY1', 120); //체크 제약조건위배되서 안들어가짐

alter table subjectt add constraint uq_subject_subid unique (subid);

: not null은 보장안함 . no duplicate는 보장

inseert into subjectt values (null, '없음0'); -널값의 중복은 허용

inseert into subjectt values ('KOR1', '없음0'); -중복 허용 x

권장사항 : constraint는 테스트 끝나고서... (회원가입 담당자가 일 다안한 상황에서 게시판 담당자가 테스트 들어가려면?)

alter table scoret drop constraint ck_scoret_score;

alter table subject drop constraint uq_subjectt_subid;

alter table subject drop constraint pk_subjectt_stid;

create table bangmyung_t(no int, gul varchar(100), the_time date);

create sequence seq_bangmyng;

insert into bangmyung_t values (seq_bangmyung.nextval, '만나서 반갑습니다.', sysdate);

...

mysql > create table bangmyung_t (

no int auto_increment primary key,

gul varchar(100),

the_time datetime);



//함수로 선언해서 재사용성을 높였다.

sql문장에서 에러 -> `stmt.executeUpdate(sql)`에서 예외 발생 

-> conn.close() 실행 안한다 -> 큰에러

```java
package day0725.test103;

import java.sql.*;

public class Test103 {
    /*
        executeUpdate 상황엣 에러나도 conn.close() 는 되어야 한다? ㅇㅇ
        finally 영역은 try 영역에서 에러가 나건 안나건 무조건 실향한다.
        :stmt.close() conn.close() 를 finally 로 옮김.
        : 변수선언 정리
        getConnection() 에서 에러나면? conn 과 stmt 는 null 인 채로 finally 행
        그러면 stmt.close()가 참조형변수가 null이므로 메소드를 실행할 수 없어 에러가남
        stmt.close() conn.close() 가 stmt , conn 이 null 이 아닐때만
            호출하도록 개선했다.
            : 프로젝트 때 마르고 닳도록 쓸거다
     */
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Class.forName("oracle.jdbc.driver.OracleDriver");
        addGul("HelloApple");

    }

    private static void addGul(String gul) throws ClassNotFoundException, SQLException {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521/XE", "HR", "HR");

            stmt = conn.createStatement();
            String sql = "insert into bangmyung_t values (seq_bangmyung.nextval,'" + gul + "' , sysdate)";
            stmt.executeUpdate(sql);
        } catch (Exception e) {
            throw e;
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
            System.out.println("all closesd");
        }


    }
/*
       함수로 선언해서 재사용성을 높였다.
       SQL 문장에서 에러 -> stmt.executeUpdate(sql) 에서 예외발생
       -> conn.close() 실행 안된다. -> 이건 좀 크다  (conn 은 빨리 끊어야)

    private static void addGul(String gul) throws ClassNotFoundException, SQLException {

        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521/XE", "HR", "HR");

        Statement stmt = conn.createStatement();
        String sql = "insert into bangmyung_t values (seq_bangmyung.nextval,'" + gul + "' , sysdate)";
        stmt.executeUpdate(sql);

        stmt.close();
        conn.close();
    }
*/
}
```



```java
package day0725.test104;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class BangMyungVO {
    private Integer no = null;
    private String gul = null;
    private String theTime = null;

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public String getGul() {
        return gul;
    }

    public void setGul(String gul) {
        this.gul = gul;
    }

    public String getTheTime() {
        return theTime;
    }

    public void setTheTime(String theTime) {
        this.theTime = theTime;
    }
}

public class Test104 {
    public static List<BangMyungVO> findAll() throws Exception {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<BangMyungVO> ls = new ArrayList<BangMyungVO>();
        try {
            conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521/XE", "HR", "HR");
            stmt = conn.createStatement();
            String sql = "select * from bangmyung_t";
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                BangMyungVO vo = new BangMyungVO();
                vo.setNo(rs.getInt("no"));
                vo.setGul(rs.getString("gul"));
                vo.setTheTime(rs.getString("the_time"));
                ls.add(vo);
            }
        } catch (SQLException e) {
            throw e; //에러나면 잡고 finally 거치고 다시 발생.
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }

        }


        return ls;
    }

    public static void main(String[] args) throws Exception {
        Class.forName("oracle.jdbc.driver.OracleDriver");
        List<BangMyungVO> ls2 = findAll();
        for (BangMyungVO t : ls2) {
            System.out.println(t.getNo() + t.getGul() + t.getTheTime());
        }
    }
}
```



```java
package javaclass;
public class BangMyungVO {
    private Integer no = null;
    private String gul = null;
    private String theTime = null;

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public String getGul() {
        return gul;
    }

    public void setGul(String gul) {
        this.gul = gul;
    }

    public String getTheTime() {
        return theTime;
    }

    public void setTheTime(String theTime) {
        this.theTime = theTime;
    }
}

```

```java
package javaclass;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BangMyungDAO {
    static {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    public static void addGul(String gul) throws ClassNotFoundException, SQLException {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521/XE", "HR", "HR");

            stmt = conn.createStatement();
            String sql = "insert into bangmyung_t values (seq_bangmyung.nextval,'" + gul + "' , sysdate)";
            stmt.executeUpdate(sql);
        } catch (Exception e) {
            throw e;
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
            System.out.println("all closesd");
        }


    }


    public static List<BangMyungVO> findAll() throws Exception {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<BangMyungVO> ls = new ArrayList<BangMyungVO>();
        try {
            conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521/XE", "HR", "HR");
            stmt = conn.createStatement();
            String sql = "select * from bangmyung_t";
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                BangMyungVO vo = new BangMyungVO();
                vo.setNo(rs.getInt("no"));
                vo.setGul(rs.getString("gul"));
                vo.setTheTime(rs.getString("the_time"));
                ls.add(vo);
            }
        } catch (SQLException e) {
            throw e; //에러나면 잡고 finally 거치고 다시 발생.
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }

        }

        return ls;
    }
}

```

```java
package javaclass;

import java.sql.SQLException;
import java.util.List;

public class Test105 {
    public static void main(String[] args) throws Exception {
        BangMyungDAO.addGul("끝이 보이냐?");
        List<BangMyungVO> ls = BangMyungDAO.findAll();
        for(BangMyungVO vo : ls){
            System.out.println(vo.getNo()+"\t"+vo.getGul()+"\t"+vo.getTheTime());
        }

    }
}
```

.