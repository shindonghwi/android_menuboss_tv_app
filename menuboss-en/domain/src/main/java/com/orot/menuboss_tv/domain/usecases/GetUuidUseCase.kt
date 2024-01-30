package com.orot.menuboss_tv.domain.usecases

import com.orot.menuboss_tv.domain.repository.LocalRepository
import javax.inject.Inject

class GetUuidUseCase @Inject constructor(private val localRepository: LocalRepository) {
    suspend operator fun invoke(): String = localRepository.getUUID()
}