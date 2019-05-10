---
typora-copy-images-to: image
---

# 算法设计

我们采用面向对象的思想进行算法设计，建立电梯类Elevator，调度器类Controller，界面上发出命令的电梯内部和外部的按钮均与Controller里响应的属性绑定，按钮按下的相关事件处理函数即使分配内外请求的算法。

为了模拟实际，采用多线程的思想，每一部电梯的运行均开辟一个线程。

为了更好地减少Controller和Elevator耦合度，Controller并不直接去干预电梯的运行，只是将电梯内部的请求或外部的请求加入指定电梯的任务队列内，由电梯不断地去响应各自的上行请求队列和下行请求队列。

## Elevator类

每个电梯内部均有待响应的请求队列，请求队列不区分是电梯内部按钮的请求还是外部楼层按钮的请求，只区分是上行请求还是下行请求。上行请求队列是`upQueue`,下行请求队列是`downQueue`。

这里的`upQueue`和`downQueue`均为优先队列，但不完全是java自带的`PriorityQueue<Integer>`,而是`MyPriQueue`,其对`PriorityQueue`进行了继承，我重写了`PriorityQueue`的add方法，以实现优先队列里边没有重复的数字，这样就不会出现同一个楼层按钮连续按了多次电梯也响应多次的bug。

由于各电梯线程运行时主要执行run方法，看run方法的大体架构即很容易理解电梯是如何响应上行和下行请求的。

```java
@Override
    public void run() {
        while(true){
            while(upQueue.size() > 0) {
                status = UP;
                mySleep(500);
                if(upQueue.peek() != currentFloor) {
                    currentFloor++;
                }
				...
                if(upQueue.peek() == currentFloor) {
                    upQueue.poll();
                	...
                }
            }
            while(downQueue.size() > 0) {
                status = DOWN;
                mySleep(500);
                if(downQueue.peek() != currentFloor) {
                    currentFloor--;
                }
                ...
                if(downQueue.peek() == currentFloor) {
                    downQueue.poll();
                	...
                }
            }
            status = PAUSE;
        }
    }
```



电梯的运行分三个状态：UP，DOWN，和PAUSE，由以上代码可以看出，只要电梯在上升且有待响应的上升请求，电梯的状态就是UP，上升过程中的中途停靠并不会改变电梯的状态，且电梯不会在还有上行请求未处理的情况下去响应下行请求，这也与我们实际生活相符。当上升请求都响应完了，如果有下行请求的话电梯转为DOWN状态，只有当电梯没有任何待响应的请求是电梯的状态才是PAUSE。电梯下行和上行的写法完全是对称的，所以下行不再赘述。其实可以看到，比如电梯从1楼到达20楼，不管中途如何停靠，电梯的状态始终是UP，再从20楼载客到1楼时，电梯的状态也始终是DOWN，电梯没有任何待响应的请求时，状态才会变为PAUSE。在理解了电梯三个状态的前提下，就十分容易理解Controller里相关的调度算法。

# Controller类

