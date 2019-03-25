package nam.tran.data.interactor

import nam.tran.data.api.IApi
import nam.tran.data.executor.AppExecutors
import javax.inject.Inject

class SingerUseCase @Inject internal constructor(appExecutors: AppExecutors, iApi: IApi) : ISingerUseCase {

}