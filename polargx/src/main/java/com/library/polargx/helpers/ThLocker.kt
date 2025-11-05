package com.library.polargx.helpers

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * A thread-safe locker that can suspend multiple coroutines until unlocked.
 * Equivalent to iOS ThLocker actor.
 */
class ThLocker {
    private val mutex = Mutex()
    private var isLocked = false
    private val continuations = mutableListOf<Continuation<Unit>>()
    
    /**
     * Wait until the locker is unlocked.
     * If already unlocked, returns immediately.
     * If locked, suspends until unlock() is called.
     */
    suspend fun waitForUnlock() {
        Logger.d("","waitForUnlock: this=$this, isLocked=$isLocked")
        val shouldWait = mutex.withLock {
            if (!isLocked) {
                return // Not locked, proceed immediately
            }
            true
        }
        
        if (shouldWait) {
            // Suspend until unlock() is called
            suspendCoroutine<Unit> { continuation ->
                // Must acquire lock to safely add to list
                while (!mutex.tryLock()) {
                    // Spin until we can acquire lock
                }
                continuations.add(continuation)
                mutex.unlock()
            }
        }
    }
    
    /**
     * Unlock and resume all waiting coroutines.
     */
    suspend fun unlock() {
        mutex.withLock {
            if (!isLocked) return
            isLocked = false
            
            // Resume all waiting coroutines
            val toResume = continuations.toList()
            continuations.clear()
            
            // Resume outside the lock to avoid deadlock
            toResume.forEach { continuation ->
                continuation.resume(Unit)
            }
        }
    }
    
    /**
     * Lock the locker, blocking new requests.
     */
    suspend fun lock() {
        mutex.withLock {
            isLocked = true
        }
    }
    
    /**
     * Check if currently locked.
     */
    suspend fun isLocked(): Boolean {
        mutex.withLock {
            return isLocked
        }
    }
}

