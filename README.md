## Overview

이 프로젝트는 tokyo-tyrant의 java client를 만드는 시작 부분을 TDD로 진행하는 과정을 보여줍니다

Kent Beck의 [원본 video](http://pragprog.com/screencasts/v-kbtdd/test-driven-development)를 저자의 양해를 구해서 약간의 각색, 부연 설명을 추가했습니다

이 예제는 mac, [home brew](https://github.com/Homebrew/brew), IntelliJ 를 사용하는 것을 가정하고 진행됩니다.

## 1. Create TODO List

Kent Beck은 TDD의 첫번째 단계는 해야 할 목록 즉 TODO 목록을 작성하는 것이라고 합니다.

아래와 같이 TODO를 작성.

```language
TODO
Function List
--------------------------------
* put(0x10)
* get(0x30)
* remove(0x20)
* vanish(0x72)
* iterator
* size(0x80)
* reset(0x50)
* get next key(0x51)
* not found(1)
* success(0)

port    1978
--------------------------------

packet structure
--------------------------------
1   OPERATION_PREFIX(0xC8)
1   OPERATION_CODE
4   key length
4   value length
n   key
n   value
```

TODO에는 구현할 기능 목록, tyrant 접속 포트번호, TCP Packet 구조 등을 기록.