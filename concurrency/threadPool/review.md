# 🎯 LLD Interview Review — Custom Thread Pool

> **Candidate Implementation:** Custom Thread Pool with Blocking Queue  
> **Files Reviewed:**  
> [CustomThreadPool.java](file:///Users/akshat/Desktop/LowLevelDesign/concurrency/threadPool/CustomThreadPool.java) · [CustomBlockingQueue.java](file:///Users/akshat/Desktop/LowLevelDesign/concurrency/threadPool/CustomBlockingQueue.java) · [Worker.java](file:///Users/akshat/Desktop/LowLevelDesign/concurrency/threadPool/Worker.java) · [Main.java](file:///Users/akshat/Desktop/LowLevelDesign/concurrency/threadPool/Main.java)

---

## 📊 Overall Verdict

| Dimension | Score | Notes |
|-----------|-------|-------|
| **Correctness** | 6/10 | Core flow works, but shutdown has race conditions and edge cases are unhandled |
| **Concurrency** | 5/10 | Synchronization on the queue is correct, but lacks shutdown coordination and interrupt safety |
| **OOP / SOLID** | 6/10 | Clean separation of Worker, Queue, and Pool — but lacks interfaces and extensibility hooks |
| **Extensibility** | 4/10 | Hard to extend without modifying existing classes; no strategy/policy points |
| **Edge Case Handling** | 3/10 | Missing: null tasks, shutdown rejection, pool size validation, graceful drain |
| **Code Quality** | 7/10 | Clean and readable; good naming, logical structure |
| **Overall** | **5.5/10** | Solid foundation — demonstrates understanding of producer-consumer, but needs hardening for a strong LLD round |

---

## ✅ What's Done Well

### 1. Correct Producer-Consumer Pattern
The core architecture is sound — the pool submits tasks to a shared blocking queue, and workers pull from it:
```
Main → submit(task) → CustomBlockingQueue ← pop() ← Worker threads
```
This is the textbook approach and the right mental model.

### 2. Proper Use of `wait()`/`notifyAll()` with `while` Loop
```java
// CustomBlockingQueue.java — Lines 17-19
while(queue.isEmpty()){
    wait();
}
```
Using a `while` loop (not `if`) around `wait()` correctly handles **spurious wakeups**. This is a detail many candidates miss — good job.

### 3. Clean Separation of Concerns
- `CustomBlockingQueue` — owns synchronization and task storage
- `Worker` — owns the task execution loop
- `CustomThreadPool` — orchestrates threads and exposes the API

Each class has a single, clear responsibility.

### 4. Worker Exception Handling
```java
// Worker.java — Lines 20-25
} catch (InterruptedException e) {
    break;
} catch (Exception e) {
    System.out.println("Error executing task: " + e.getMessage());
}
```
A failing task doesn't crash the worker thread — the pool remains functional. This is important for resilience.

---

## 🔴 Critical Issues (Would Probe in Interview)

### Issue 1: Race Condition in `shutDown()` — Tasks May Be Lost

```java
// CustomThreadPool.java — Lines 24-28
public void shutDown() {
    for (Thread worker : workers) {
        worker.interrupt();
    }
}
```

**Problem:** `shutDown()` immediately interrupts all workers. If a worker is blocked on `pop()` → `wait()`, the `InterruptedException` is thrown, the worker breaks out, and **all remaining tasks in the queue are silently discarded**.

**Interviewer would ask:**  
> *"What happens if I submit 10 tasks and immediately call `shutDown()`? Can you guarantee all 10 will execute?"*

**Expected answer:** No — you need two shutdown modes:
- **`shutdown()`** — stop accepting new tasks, but drain the queue first
- **`shutdownNow()`** — interrupt immediately, return un-executed tasks

```java
// Graceful shutdown sketch
public void shutdown() {
    isShutdown = true;
    // Workers should check: if (isShutdown && queue.isEmpty()) break;
}

public List<Runnable> shutdownNow() {
    isShutdown = true;
    for (Thread worker : workers) {
        worker.interrupt();
    }
    return taskQueue.drain(); // return un-executed tasks
}
```

### Issue 2: No Post-Shutdown Task Rejection

```java
// CustomThreadPool.java — Lines 20-22
public void submit(Runnable task) {
    taskQueue.push(task);  // no guard!
}
```

**Problem:** After `shutDown()` is called, `submit()` still pushes tasks into the queue. These tasks will never be executed (workers are interrupted), wasting memory and misleading the caller.

**Interviewer would ask:**  
> *"What happens if another thread calls `submit()` after `shutDown()`?"*

**Fix:** Add an `isShutdown` flag:
```java
private volatile boolean isShutdown = false;

public void submit(Runnable task) {
    if (isShutdown) {
        throw new RejectedExecutionException("Pool is shut down");
    }
    taskQueue.push(task);
}
```

### Issue 3: `shutDown()` Doesn't Wait for Completion

**Problem:** The caller of `shutDown()` has no way to know when workers have actually finished. In `Main.java`, the main thread could exit before workers complete their tasks.

**Interviewer would ask:**  
> *"How would the caller wait until the pool is fully terminated?"*

**Fix:** Add `awaitTermination()` or have `shutDown()` join:
```java
public void shutDown() throws InterruptedException {
    isShutdown = true;
    for (Thread worker : workers) {
        worker.interrupt();
    }
    for (Thread worker : workers) {
        worker.join(); // wait for each worker to finish
    }
}
```

---

## 🟡 Design Issues (Follow-Up Questions an Interviewer Would Ask)

### Issue 4: Unbounded Queue — No Back-Pressure

```java
// CustomBlockingQueue.java — Line 8
private final Queue<Runnable> queue = new LinkedList<>();
```

**Interviewer's question:**  
> *"What happens if the producer submits tasks faster than workers can process them?"*

The queue grows without limit → **OOM risk**. A real blocking queue should have a capacity:
- `push()` blocks when the queue is full (back-pressure)
- `pop()` blocks when the queue is empty (already done ✅)

This shows understanding of both sides of producer-consumer.

### Issue 5: No Interface / Generics on the Queue

```java
public class CustomBlockingQueue {  // concrete class, Runnable-only
```

**Interviewer's question:**  
> *"Can I reuse your blocking queue for something other than Runnables?"*

No — it's hardcoded to `Runnable`. A stronger design:
```java
public class CustomBlockingQueue<T> {
    private final Queue<T> queue = new LinkedList<>();
    public synchronized void push(T item) { ... }
    public synchronized T pop() throws InterruptedException { ... }
}
```
Also consider extracting a `BlockingQueue<T>` interface for testability and the Dependency Inversion principle.

### Issue 6: `Integer` vs `int` for Pool Size

```java
// CustomThreadPool.java — Line 5
private final Integer poolSize;
```

**Interviewer's question:**  
> *"Why use `Integer` here? What happens if someone passes `null`?"*

`new Thread[null]` → `NullPointerException` via auto-unboxing. Use `int`. Also validate:
```java
if (poolSize <= 0) throw new IllegalArgumentException("Pool size must be > 0");
```

### Issue 7: No Input Validation on `submit()`

**Interviewer's question:**  
> *"What if someone calls `submit(null)`?"*

Currently, `null` gets pushed into the queue. When a worker pops it, the `if (task != null)` check silently drops it. The caller thinks the task was accepted but it's thrown away — a **silent failure**.

Better to **fail fast**:
```java
public void submit(Runnable task) {
    if (task == null) throw new NullPointerException("Task cannot be null");
    taskQueue.push(task);
}
```

---

## 🔵 Minor / Style Observations

| Item | Comment |
|------|---------|
| **Thread naming** | Workers are unnamed (`Thread-0`, `Thread-1`). Use `new Thread(worker, "pool-thread-" + i)` for debuggability. |
| **`poolSize` field is unused** | After construction, `poolSize` is never read. It can be removed or exposed via a getter. |
| **`notifyAll()` vs `notify()`** | `notifyAll()` in `push()` is safe but wakes all waiting threads. Since only one task is added per `push()`, `notify()` is sufficient and slightly more efficient. |
| **Logging** | `System.out.println` for errors is fine for learning, but in production use a logger. |

---

## 🧠 Follow-Up Interview Questions You Should Prepare For

These are questions an LLD interviewer would naturally ask based on this design:

### Concurrency Deep-Dives
1. **"Why `while` and not `if` before `wait()`?"** — Spurious wakeups. Always re-check the condition.
2. **"Why `synchronized` method vs `ReentrantLock`?"** — Trade-offs: `synchronized` is simpler, `ReentrantLock` gives `tryLock()`, separate `Condition` objects, fairness policies.
3. **"What happens if a task itself calls `submit()` on the same pool?"** — Works, but with a single-threaded pool + bounded queue, this can **deadlock** (task blocks on `push()`, worker can't proceed).

### Extensibility Questions
4. **"How would you add a task rejection policy?"** — Strategy pattern: `RejectedExecutionHandler` interface with implementations like `AbortPolicy`, `CallerRunsPolicy`, `DiscardPolicy`.
5. **"How would you support `Future<T>` return values from `submit()`?"** — Wrap the `Runnable`/`Callable` in a `FutureTask<T>`, push that into the queue, return the `Future<T>` to the caller.
6. **"How would you make the pool size dynamic?"** — Add `setCorePoolSize()` / `setMaxPoolSize()`, spin up/down workers based on queue depth. See `ThreadPoolExecutor`'s approach.

### Production Readiness
7. **"How would you monitor the pool?"** — Expose metrics: active thread count, queue depth, completed task count, rejected task count.
8. **"How would you handle a task that runs forever?"** — Task timeout via `Future.get(timeout)` or a watchdog thread that interrupts long-running tasks.

---

## 📈 What Would Take This from 5.5 → 8+

To make this implementation interview-ready, add these in priority order:

```
1. ✅ Shutdown flag + task rejection        (shows production thinking)
2. ✅ Bounded queue with back-pressure      (shows full producer-consumer mastery)
3. ✅ awaitTermination() / join()            (shows lifecycle management)
4. ✅ Generics on the queue                  (shows OOP / SOLID awareness)
5. ✅ Input validation (null, pool size)     (shows defensive programming)
6. 🌟 Future<T> support from submit()       (differentiator — shows depth)
```

The foundation is solid. The gaps are mostly around **lifecycle management**, **defensive programming**, and **extensibility** — all of which are exactly what interviewers probe for in an LLD round.
