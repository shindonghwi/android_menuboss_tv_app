package com.orot.menuboss_tv_kr.domain.usecases.local

import com.orot.menuboss_tv_kr.domain.repository.LocalRepository
import javax.inject.Inject

class PatchUuidUseCase @Inject constructor(private val localRepository: LocalRepository) {
    suspend operator fun invoke(uuid: String) = localRepository.setUUID(uuid)
}
