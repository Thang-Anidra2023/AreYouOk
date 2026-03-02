package com.anidra.areyouok.data.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.anidra.areyouok.data.repositories.EmergencyContactsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import retrofit2.HttpException
import java.io.IOException

@HiltWorker
class EmergencyContactsSyncWorker @AssistedInject constructor(
    private val repository: EmergencyContactsRepository,
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            repository.syncServerWithLocal(
                restoreFromServerWhenLocalEmpty = true
            )
            Result.success()
        } catch (e: IOException) {
            Result.retry()
        } catch (e: HttpException) {
            if (e.code() in 500..599) {
                Result.retry()
            } else {
                Result.failure()
            }
        } catch (_: Exception) {
            Result.retry()
        }
    }
}