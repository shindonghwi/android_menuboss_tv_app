package com.orot.menuboss_tv.data.mapper

import com.orot.menuboss_tv.data.models.DeviceInfoDTO
import com.orot.menuboss_tv.domain.entities.DeviceInfo
import javax.inject.Inject

class DeviceInfoMapper @Inject constructor() {

    fun mapFromDTO(dto: DeviceInfoDTO): DeviceInfo {
        return DeviceInfo(
            status = dto.status,
            tv = mapTV(dto.tv),
            business = dto.business?.let { mapBusiness(it) },
            product = dto.product?.let { mapProduct(it) },
            hasPlan = dto.hasPlan
        )
    }

    private fun mapTV(tvDTO: DeviceInfoDTO.TV): DeviceInfo.TV {
        return DeviceInfo.TV(
            name = tvDTO.name,
            accessToken = tvDTO.accessToken,
            screenUrl = tvDTO.screenUrl,
            code = tvDTO.code,
            qrUrl = tvDTO.qrUrl
        )
    }
    private fun mapBusiness(tvDTO: DeviceInfoDTO.Business): DeviceInfo.Business {
        return DeviceInfo.Business(
            name = tvDTO.name,
        )
    }
    private fun mapProduct(tvDTO: DeviceInfoDTO.Product): DeviceInfo.Product {
        return DeviceInfo.Product(
            title = tvDTO.title,
        )
    }

    // ... Similarly, map other nested DTOs if needed
}
