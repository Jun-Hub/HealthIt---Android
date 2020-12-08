package io.jun.healthit.util

import com.chad.library.adapter.base.entity.MultiItemEntity
import io.jun.healthit.model.data.ParentRoutine
import io.jun.healthit.model.data.Routine

//공통으로 사용되는 상수값들 설정
object Setting {

        const val GALLERY_REQUEST_CODE = 0
        const val TAKE_PHOTO_REQUEST_CODE = 1
        const val TOTAL_SIZE_LIMIT = 1160000
        const val IMAGE_SIZE_LIMIT = 9000000
        const val IMAGE_SIZE_LIMIT_MB = IMAGE_SIZE_LIMIT / 1000000
        const val MAX_PHOTO = 7
        const val MAX_RECORD = 50
        const val MAX_SET = 20

        const val DECORATOR_RADIUS = 11f

        const val UPDATE_REQUEST_CODE = 2020

        val WORK_OUT_LIST = arrayOf("스쿼트", "런지", "덤벨 런지", "워킹 런지", "덤벨 스쿼트",
            "레그 익스텐션", "레그컬", "레그프레스", "파워 레그프레스", "머신 스쿼트", "핵스쿼트", "스텝업",  "프론트 스쿼트", "카프레이즈",   //이상 하체운동
            "푸쉬업", "벤치프레스", "인클라인 벤치프레스", "디클라인 벤치프레스", "뎀벨 벤치프레스",
            "덤벨 인클라인 벤치프레스", "덤벨 인클라인 플라이", "덤벨 플라이", "풀오버", "케이블 크로스오버", "딥스",
            "어시스트 딥스",
            "중량 딥스", "중량 푸시업", "체스트 프레스", "체스트 플라이", "버터플라이", "딥스 머신",  //이상 가슴운동
            "풀업", "중량 풀업", "패러럴그립 풀업", "친업", "중량 친업", "데드리프트", "벤트오버 바벨로우", "쉬러그",
            "덤벨 데드리프트", "컨벤셔널 데드리프트", "루마니안 데드리프트", "스모 데드리프트", "스티프레그 데드리프트",
            "벤트오버 덤벨로우", "원암 덤벨로우", "랫풀다운", "백익스텐션", "어시스트 풀업", "굿모닝", "점핑 풀업",
            "암풀다운", "티바로우", "펜들레이 로우", "슈퍼맨", "시티드 로우",                     //이상 등운동
            "사이드 레터럴레이즈", "밀리터리프레스", "오버헤드프레스", "덤벨 숄더프레스", "업라이트로우", "프론트레이즈",
            "벤트오버 레터럴레이즈", "리버스 플라이", "페이스풀",                                //이상 어깨운동
            "내로우그립 벤치프레스", "킥백", "원암 익스텐션", "케이블 푸쉬다운", "트라이셉스 익스텐션", "오버헤드 트라이셉스 익스텐션",
            "케이블 트라이셉스 익스텐션", "케이블 트라이셉스 푸쉬다운",                          //이상 삼두운동
            "바벨컬", "덤벨컬", "해머컬", "프리쳐컬", "암컬", "이지바컬", "케이블 컬", "케이블 해머컬",//이상 이두운동
            "케틀벨 스쿼트")




        private val fullBodyParent1 = ParentRoutine("트리거 초급자 무분할 루틴(월)")
        private val fullBodyRoutine1 = Routine("스쿼트 (4~6)x(8~12)\n" +
                "래터럴레이즈 (4~6)x(8~12)\n" +
                "내로우그립 시티드로우 (4~6)x(8~12)\n" +
                "벤치프레스 (4~6)x(8~12)\n" +
                "크런치 (4~6)x(8~12)",
            null,
            "https://www.youtube.com/watch?v=gLmJBJGRjSU")

        private val fullBodyParent2 = ParentRoutine("트리거 초급자 무분할 루틴(수)")
        private val fullBodyRoutine2 = Routine(
                "데드리프트 (4~6)x(8~12)\n" +
                "ohp (4~6)x(8~12)\n" +
                "랫풀다운 (4~6)x(8~12)\n" +
                "인클라인 벤치프레스 (4~6)x(8~12)\n" +
                "사이드 크런치 (4~6)x(8~12)",
            null,
            "https://www.youtube.com/watch?v=gLmJBJGRjSU")

        private val fullBodyParent3 = ParentRoutine("트리거 초급자 무분할 루틴(금)")
        private val fullBodyRoutine3 = Routine(
                "스쿼트 (4~6)x(8~12)\n" +
                "래터럴레이즈 (4~6)x(8~12)\n" +
                "와이드그립 시티드 로우 (4~6)x(8~12)\n" +
                "벤치프레스 (4~6)x(8~12)\n" +
                "크런치 (4~6)x(8~12)",
            null,
            "https://www.youtube.com/watch?v=gLmJBJGRjSU")

        private val fullBodyParent4 = ParentRoutine("강경원 초보자 무분할 루틴1")
        private val fullBodyRoutine4 = Routine(
                "머신 벤치프레스 5x(8~12)\n" +
                "랫풀다운 5x(8~12)\n" +
                "머신 숄더프레스 5x(8~12)\n" +
                "바벨/덤벨컬 5x(8~12)\n" +
                "레그 익스텐션 5x(8~12)\n" +
                "레그컬 5x(8~12)\n" +
                "레그레이즈 5x(8~12)\n" +
                "플랭크 5x(8~12)",
                null,
                "https://www.youtube.com/watch?v=sCMXK2WO3n4")

        private val fullBodyParent5 = ParentRoutine("강경원 초보자 무분할 루틴2")
        private val fullBodyRoutine5 = Routine(
                "머신 벤치프레스 5x(8~12)\n" +
                "랫풀다운 5x(8~12)\n" +
                "바벨/덤벨컬 5x(8~12)\n" +
                "스쿼트 5x(8~12)\n" +
                "레그 익스텐션 5x(8~12)\n" +
                "레그컬 5x(8~12)\n" +
                "크런치 5x(8~12)\n" +
                "슈퍼맨 5x(8~12)",
            null,
            "https://www.youtube.com/watch?v=sCMXK2WO3n4")

        private val fullBodyParent6 = ParentRoutine("기본 무분할 루틴")
        private val fullBodyRoutine6 = Routine("스쿼트 5x(8~12)\n" +
                "벤치프레스 5x(8~12)\n" +
                "풀업 5x(8~12)\n" +
                "밀리터리프레스 5x(8~12)\n" +
                "행잉/캡틴스체어 레그레이즈 5x(8~12)",
            "팔운동 필요시 추가",
            null)


        private fun fullBodyRoutineList(): ArrayList<MultiItemEntity> {
            val routineList = ArrayList<MultiItemEntity>()
            fullBodyParent1.addSubItem(fullBodyRoutine1)
            fullBodyParent2.addSubItem(fullBodyRoutine2)
            fullBodyParent3.addSubItem(fullBodyRoutine3)
            fullBodyParent4.addSubItem(fullBodyRoutine4)
            fullBodyParent5.addSubItem(fullBodyRoutine5)
            fullBodyParent6.addSubItem(fullBodyRoutine6)
            routineList.add(fullBodyParent6)
            routineList.add(fullBodyParent4)
            routineList.add(fullBodyParent5)
            routineList.add(fullBodyParent1)
            routineList.add(fullBodyParent2)
            routineList.add(fullBodyParent3)

            return routineList
        }

        val fullBodyRoutineList = fullBodyRoutineList()


        private val split2dayParent1 = ParentRoutine("기본 2분할 루틴(상체)")
        private val split2dayRoutine1 = Routine(
                "벤치프레스 5x(8~12)\n" +
                "풀업 5x(8~12)\n" +
                "밀리터리프레스 5x(8~12)\n" +
                "바벨로우 5x(8~12)\n" +
                "인클라인 덤벨 벤치프레스 5x(8~12)\n" +
                "사이드 래터럴 레이즈 3x(8~12)\n" +
                "벤트오버 래터럴 레이즈 3x(8~12)\n" +
                "행잉/캡틴스체어 레그레이즈 5x(8~12)",
            null,
            null)

        private val split2dayParent2 = ParentRoutine("기본 2분할 루틴(하체)")
        private val split2dayRoutine2 = Routine(
                "스쿼트 5x(8~12)\n" +
                "파워 레그프레스 5x(8~12)\n" +
                "이지 바벨컬 5x(8~12)\n" +
                "트라이셉스 익스텐션 5x(8~12)\n" +
                "행잉/캡틴스체어 레그레이즈 5x(8~12)",
            null,
            null)

        private val split2dayParent3 = ParentRoutine("길브로(정봉길) 2분할 루틴(상체)")
        private val split2dayRoutine3 = Routine("덤벨벤치프레스 4x8\n" +
                "벤치덤벨로우 3x12\n" +
                "오버헤드프레스 3x8\n" +
                "중량 패러럴그립풀업 3x7\n" +
                "인클라인덤벨벤치 3x8\n" +
                "벤트오버 레터럴 레이즈 4x12\n" +
                "사이드 레터럴 레이즈 3x15",
            null,
            "https://www.youtube.com/watch?v=gNFm10941jU")

        private val split2dayParent4 = ParentRoutine("길브로(정봉길) 2분할 루틴(하체)")
        private val split2dayRoutine4 = Routine("스쿼트 5x7\n" +
                "스모 데드 4x5\n" +
                "바벨 런지 3x20\n" +
                "파워레그프레스 3x30\n" +
                "레그 익스텐션 3x11\n" +
                "덤벨컬 4x10",
            "팔운동은 이두 삼두 중 본인 약점인 부분을 진행",
            "https://www.youtube.com/watch?v=kaoOKJDIOtI")

        private val split2dayParent5 = ParentRoutine("트리거 중급자 2분할 루틴(하체, 어깨)")
        private val split2dayRoutine5 = Routine("프론트 스쿼트 (2~4)x(8~12)\n" +
                "루마니안 데드리프트 (2~4)x(8~12)\n" +
                "오버헤드프레스 (2~4)x(8~12)\n" +
                "케이블 리버스 플라이 (2~4)x(8~12)\n" +
                "웨이티트 크런치 (4~6)x(8~12)",
            null,
            "https://www.youtube.com/watch?v=ZFQhPc0LJP4")

        private val split2dayParent6 = ParentRoutine("트리거 중급자 2분할 루틴(등, 가슴)")
        private val split2dayRoutine6 = Routine("랫풀다운 (2~4)x(8~12)\n" +
                "원암 덤벨로우 (2~4)x(8~12)\n" +
                "덤벨 벤치프레스 (2~4)x(8~12)\n" +
                "인클라인 바벨 벤치프레스 (2~4)x(8~12)\n" +
                "암컬 (3~6)x(8~12)",
            null,
            "https://www.youtube.com/watch?v=ZFQhPc0LJP4")

        private fun split2dayRoutineList(): ArrayList<MultiItemEntity> {
            val routineList = ArrayList<MultiItemEntity>()
            split2dayParent1.addSubItem(split2dayRoutine1)
            split2dayParent2.addSubItem(split2dayRoutine2)
            split2dayParent3.addSubItem(split2dayRoutine3)
            split2dayParent4.addSubItem(split2dayRoutine4)
            split2dayParent5.addSubItem(split2dayRoutine5)
            split2dayParent6.addSubItem(split2dayRoutine6)
            routineList.add(split2dayParent1)
            routineList.add(split2dayParent2)
            routineList.add(split2dayParent3)
            routineList.add(split2dayParent4)
            routineList.add(split2dayParent5)
            routineList.add(split2dayParent6)

            return routineList
        }

        val split2dayRoutineList = split2dayRoutineList()


        private val split3dayParent1 = ParentRoutine("기본 3분할 루틴(상체 밀기)")
        private val split3dayRoutine1 = Routine("벤치프레스 4x(8~12)\n" +
                "밀리터리프레스 4x(8~12)\n" +
                "인클라인 덤벨 벤치프레스 4x(8~12)\n" +
                "딥스 4x(8~12)\n" +
                "사이드 래터럴 레이즈 4x(8~12)\n" +
                "벤트오버 래터럴 레이즈 4x(8~12)\n" +
                "트라이셉스 익스텐션 5x(8~12)",
            null,
            null)

        private val split3dayParent2 = ParentRoutine("기본 3분할 루틴(상체 당기기)")
        private val split3dayRoutine2 = Routine("풀업 4x(8~12)\n" +
                "루마니안 데드리프트 4x(8~12)\n" +
                "바벨로우 4x(8~12)\n" +
                "랫풀다운 4x(8~12)\n" +
                "이지 바벨컬 5x(8~12)",
            null,
            null)

        private val split3dayParent3 = ParentRoutine("기본 3분할 루틴(하체)")
        private val split3dayRoutine3 = Routine("스쿼트 4x(8~12)\n" +
                "스모 데드리프트 4x(8~12)\n" +
                "파워 레그프레스 4x(8~12)\n" +
                "행잉/캡틴스체어 레그레이즈 5x(8~12)",
            null,
            null)

        private val split3dayParent4 = ParentRoutine("길브로(정봉길) 3분할 루틴(상체 밀기)")
        private val split3dayRoutine4 = Routine("벤치프레스 3x8\n" +
                "오버헤드프레스 4x8\n" +
                "인클라인 덤벨프레스 3x9\n" +
                "원암 덤벨 숄더프레스 3x9\n" +
                "중량 딥스 3x11\n" +
                "사이드 레터럴 레이즈 3x11\n" +
                "케이블 푸쉬다운 3x10",
            null,
            "https://www.youtube.com/watch?v=e6KfPOOb8f4")

        private val split3dayParent5 = ParentRoutine("길브로(정봉길) 3분할 루틴(상체 당기기)")
        private val split3dayRoutine5 = Routine("중량 풀업 3x9\n" +
                "원암 덤벨로우 3x15\n" +
                "덤벨 쉬러그 3x12\n" +
                "리어델트 레이즈 3x12\n" +
                "덤벨컬 4x10\n" +
                "랫풀다운 3x6",
            null,
            "https://www.youtube.com/watch?v=FgpGZRcT7tE")

        private val split3dayParent6 = ParentRoutine("길브로(정봉길) 3분할 루틴(하체)")
        private val split3dayRoutine6 = Routine("스모 데드리프트 3x8\n" +
                "스쿼트 머신 3x8\n" +
                "파워 레그프레스 3x15\n" +
                "컨벤셔널 데드리프트 3x10",
            null,
            "https://www.youtube.com/watch?v=SmlJJVmzdi0")

        private fun split3dayRoutineList(): ArrayList<MultiItemEntity> {
            val routineList = ArrayList<MultiItemEntity>()
            split3dayParent1.addSubItem(split3dayRoutine1)
            split3dayParent2.addSubItem(split3dayRoutine2)
            split3dayParent3.addSubItem(split3dayRoutine3)
            split3dayParent4.addSubItem(split3dayRoutine4)
            split3dayParent5.addSubItem(split3dayRoutine5)
            split3dayParent6.addSubItem(split3dayRoutine6)

            routineList.add(split3dayParent1)
            routineList.add(split3dayParent2)
            routineList.add(split3dayParent3)
            routineList.add(split3dayParent4)
            routineList.add(split3dayParent5)
            routineList.add(split3dayParent6)

            return routineList
        }

        val split3dayRoutineList = split3dayRoutineList()



        private val split4dayParent1 = ParentRoutine("기본 4분할 루틴(가슴, 이두)")
        private val split4dayRoutine1 = Routine("벤치프레스 4x(8~12)\n" +
                "인클라인 덤벨프레스 4x(8~12)\n" +
                "딥스 4x(8~12)\n" +
                "케이블 크로스오버 4x(8~12)\n" +
                "이지 바벨컬 4x(8~12)\n" +
                "덤벨컬 4x(8~12)",
            null,
            null)

        private val split4dayParent2 = ParentRoutine("기본 4분할 루틴(하체, 복근)")
        private val split4dayRoutine2 = Routine("스쿼트 4x(8~12)\n" +
                "스모 데드리프트 4x(8~12)\n" +
                "파워 레그프레스 4x(8~12)\n" +
                "런지 4x(8~12)\n" +
                "행잉/캡틴스체어 레그레이즈 5x(8~12)\n" +
                "크런치 5x(8~12)",
            null,
            null)

        private val split4dayParent3 = ParentRoutine("기본 4분할 루틴(어깨, 삼두)")
        private val split4dayRoutine3 = Routine("밀리터리프레스 4x(8~12)\n" +
                "덤벨 숄더프레스 4x(8~12)\n" +
                "사이드 래터럴 레이즈 4x(8~12)\n" +
                "벤트오버 래터럴 레이즈 4x(8~12)\n" +
                "트라이셉스 익스텐션 5x(8~12)\n" +
                "케이블 푸쉬다운 5x(8~12)",
            null,
            null)

        private val split4dayParent4 = ParentRoutine("기본 4분할 루틴(등, 복근)")
        private val split4dayRoutine4 = Routine("풀업 4x(8~12)\n" +
                "루마니안 데드리프트 4x(8~12)\n" +
                "바벨로우 4x(8~12)\n" +
                "시티드로우 4x(8~12)\n" +
                "행잉/캡틴스체어 레그레이즈 5x(8~12)\n" +
                "크런치 5x(8~12)",
            null,
            null)

        private fun split4dayRoutineList(): ArrayList<MultiItemEntity> {
            val routineList = ArrayList<MultiItemEntity>()
            split4dayParent1.addSubItem(split4dayRoutine1)
            split4dayParent2.addSubItem(split4dayRoutine2)
            split4dayParent3.addSubItem(split4dayRoutine3)
            split4dayParent4.addSubItem(split4dayRoutine4)

            routineList.add(split4dayParent1)
            routineList.add(split4dayParent2)
            routineList.add(split4dayParent3)
            routineList.add(split4dayParent4)

            return routineList
        }

        val split4dayRoutineList = split4dayRoutineList()


        private val split5dayParent1 = ParentRoutine("기본 5분할 루틴(가슴)")
        private val split5dayRoutine1 = Routine("벤치프레스 4x(8~12)\n" +
                "인클라인 덤벨프레스 4x(8~12)\n" +
                "딥스 4x(8~12)\n" +
                "머신 체스트프레스 4x(8~12)\n" +
                "케이블 크로스오버 4x(8~12)",
            "필요시 복근 추가",
            null)

        private val split5dayParent2 = ParentRoutine("기본 5분할 루틴(등)")
        private val split5dayRoutine2 = Routine("풀업 4x(8~12)\n" +
                "루마니안 데드리프트 4x(8~12)\n" +
                "바벨로우 4x(8~12)\n" +
                "원암 덤벨로우 4x(8~12)\n" +
                "시티드 로우 4x(8~12)",
            "필요시 복근 추가",
            null)

        private val split5dayParent3 = ParentRoutine("기본 5분할 루틴(어깨)")
        private val split5dayRoutine3 = Routine("밀리터리 프레스 4x(8~12)\n" +
                "덤벨 숄더프레스 4x(8~12)\n" +
                "사이드 래터럴 레이즈 4x(8~12)\n" +
                "벤트오버 래터럴 레이즈 4x(8~12)",
            "필요시 복근 추가",
            null)

        private val split5dayParent4 = ParentRoutine("기본 5분할 루틴(팔)")
        private val split5dayRoutine4 = Routine("트라이셉스 익스텐션 5x(8~12)\n" +
                "이지 바벨컬 5x(8~12)\n" +
                "케이블 푸쉬다운 5x(8~12)\n" +
                "덤벨컬 5x(8~12)",
            "필요시 복근 추가",
            null)

        private val split5dayParent5 = ParentRoutine("기본 5분할 루틴(하체)")
        private val split5dayRoutine5 = Routine("스쿼트 4x(8~12)\n" +
                "스모 데드리프트 4x(8~12)\n" +
                "파워 레그프레스 4x(8~12)\n" +
                "런지 4x(8~12)\n" +
                "레그 익스텐션 4x(8~12)",
            "필요시 복근 추가",
            null)

        private fun split5dayRoutineList(): ArrayList<MultiItemEntity> {
            val routineList = ArrayList<MultiItemEntity>()
            split5dayParent1.addSubItem(split5dayRoutine1)
            split5dayParent2.addSubItem(split5dayRoutine2)
            split5dayParent3.addSubItem(split5dayRoutine3)
            split5dayParent4.addSubItem(split5dayRoutine4)
            split5dayParent5.addSubItem(split5dayRoutine5)

            routineList.add(split5dayParent1)
            routineList.add(split5dayParent2)
            routineList.add(split5dayParent3)
            routineList.add(split5dayParent4)
            routineList.add(split5dayParent5)

            return routineList
        }

        val split5dayRoutineList = split5dayRoutineList()


        private val strengthParent1 = ParentRoutine("5x5 스트렝스 루틴A")
        private val strengthRoutine1 = Routine("스쿼트 5x5\n" +
                "벤치프레스 5x5\n" +
                "바벨로우 5x5",
            "휴식을 사이에 두고 루틴 A와 B를 번갈아 가면서 할 것. ex) A휴B휴A휴B",
            null)

        private val strengthParent2 = ParentRoutine("5x5 스트렝스 루틴B")
        private val strengthRoutine2 = Routine("스쿼트 5x5\n" +
                "데드리프트 5x5\n" +
                "밀리터리프레스 5x5",
            "휴식을 사이에 두고 루틴 A와 B를 번갈아 가면서 할 것. ex) A휴B휴A휴B",
            null)

        private val strengthParent3 = ParentRoutine("피지컬갤러리 스트렝스 루틴(월)")
        private val strengthRoutine3 = Routine("스쿼트 5x5\n" +
                "벤치 5x5\n" +
                "바벨로우 5x5",
            null,
            "https://www.youtube.com/watch?v=MwC-i3sHnhI")

        private val strengthParent4 = ParentRoutine("피지컬갤러리 스트렝스 루틴(수)")
        private val strengthRoutine4 = Routine(
                "스쿼트 5x5 (월요일 중량의 60%)\n" +
                "데드리프트 1x5\n" +
                "밀프 5x5\n" +
                "벤치 5x5 (월요일 중량의 60%)",
            null,
            "https://www.youtube.com/watch?v=MwC-i3sHnhI")

        private val strengthParent5 = ParentRoutine("피지컬갤러리 스트렝스 루틴(금)")
        private val strengthRoutine5 = Routine(
                "스쿼트 5x5 (월요일 중량의 80%)\n" +
                "중량 풀업 5x5\n" +
                "중량 딥스 5x5",
            null,
            "https://www.youtube.com/watch?v=MwC-i3sHnhI")

        private fun strengthRoutineList(): ArrayList<MultiItemEntity> {
            val routineList = ArrayList<MultiItemEntity>()

            strengthParent1.addSubItem(strengthRoutine1)
            strengthParent2.addSubItem(strengthRoutine2)
            strengthParent3.addSubItem(strengthRoutine3)
            strengthParent4.addSubItem(strengthRoutine4)
            strengthParent5.addSubItem(strengthRoutine5)

            routineList.add(strengthParent1)
            routineList.add(strengthParent2)
            routineList.add(strengthParent3)
            routineList.add(strengthParent4)
            routineList.add(strengthParent5)

            return routineList
        }
        val strengthRoutineList = strengthRoutineList()


        private val commonSenseParent1 = ParentRoutine("근육량이 증가하면 기초대사량이 매우 높아진다?")
        private val commonSense1 = Routine(
            "근육량이 증가하면 기초대사량이 높아지는 것은 사실이다. 그러나 골격근 1kg이 소비하는 기초대사량은 " +
                    "13kcal 정도로 매우 미미하다. 타고나지 않은 일반인이 평생 운동해서 얻을 수 있는 순수 골격근량은 10kg 내외다. " +
                    "이를 감안하면 선천적으로 타고난 기초대사량을 운동으로 바꾸는 건 힘들어 보인다.",
            null,
            null)

        private val commonSenseParent2 = ParentRoutine("같은 무게면 지방이 근육보다 부피가 훨씬 크다?")
        private val commonSense2 = Routine(
            "같은 무게일 때 지방이 근육보다 2~3배 정도 부피가 크다고 생각하는 사람이 많다. 근육이 지방보다 무거운 건 " +
                    "사실이지만, 사실 별 차이가 없다. 같은 무게일 때 지방은 근육보다 겨우 30%정도 더 부피가 클 뿐이다.",
            null,
            null)

        private val commonSenseParent3 = ParentRoutine("2년 안에 정말 몸짱이 될 수 있을까?")
        private val commonSense3 = Routine(
            "결론부터 말하자면 매우 힘들다. 다른 운동으로 원래부터 몸이 어느정도 만들어졌거나, 정말 타고난게 아니라면 " +
                    "2년으로는 택도 없다. 운동경력이 아예 없는 일반인 기준으로는 최소 3년 이상은 잡아야 괜찮은 몸이 나올 수 있다. " +
                    "3년도 최소이고 보통 4~5년은 해야한다. 보통 일반인들은 운동 초기에 여러 시행착오를 겪으면서 비효율적인 방법으로 " +
                    "운동하는 경우가 많기 때문이다. 참고로 몸 좋은 사람들은 시행착오 기간인 1~2년 정도를 제외하고 운동경력을 줄이는 " +
                    "경우가 대다수다.",
            null,
            null)

        private val commonSenseParent4 = ParentRoutine("몸을 키우는 데 3대 운동 중량이 정말 중요할까?")
        private val commonSense4 = Routine(
            "근력보다는 근비대 향상을 목적으로 하는 보디빌딩에서는 중량보다 고립을 통한 자극이 중요하다. 3대 운동 " +
                    "열풍이 불면서 운동 목적이 보디빌딩임에도 불구하고 파워리프팅 방식으로 운동하는 사람들이 많다. 실제로 " +
                    "유명 보디빌더나 유튜버를 봐도 3대 중량 500이 안되는 사람이 많다. 같은 벤치프레스를 해도 파워리프팅식과 " +
                    "보디빌딩식은 다르다. 점진적 과부하를 통한 증량이 필요하지만 고립이 기본이 되어야 한다. 보디빌딩식으로 운동을 " +
                    "하면 증량이 얼마나 힘든지 깨닫게 될 것이다. 자기가 무슨 목적으로 운동을 하는지 생각해보자.",
            null,
            null)

        private val commonSenseParent5 = ParentRoutine("단백질 보충제는 근육 생성에 탁월하다?")
        private val commonSense5 = Routine(
            "아니다. 단백질 보충제는 그냥 우유에서 단백질만 추출한 것 뿐이다. 애초에 식품으로 분류되기 때문에 근육 생성에 기여하는 " +
                    "특별한 성분같은 게 있을리 없다. 닭가슴살의 단백질 20g과 단백질 보충제의 단백질 20g은 같다. 심지어 단백질 흡수율은 " +
                    "자연식보다 떨어진다. 그래서 실제로 보디빌더들은 보충제는 잘 안먹고 자연식으로 단백질 섭취를 한다. " +
                    "몸짱 유튜버들이 보충제를 먹는 건 광고거나, 쇼핑몰 추천인코드 홍보로 돈을 벌기 위함이다. 단백질 보충제의 장점은 " +
                    "일반식에 비해 가격이 저렴한 것과 섭취가 간편하다는 것이 전부다.",
            null,
            null)

        private val commonSenseParent6 = ParentRoutine("운동으로 골반을 넓히는 게 가능하다?")
        private val commonSense6 = Routine(
            "골반 양옆에 있는 중둔근은 속근보다 지근 비율이 훨씬 높아서, 아무리 운동해도 커지지 않는 근육이다. " +
                    "일부 유튜버들은 이 중둔근 운동으로 골반을 넓힐 수 있다고 하는데, 당연히 사기다. 골반 넓이는 타고나는 것이며, " +
                    "before after 사진으로 골반 넓이가 달라 보이는 것은 그냥 포징의 차이다. " +
                    "이제 진실을 알게 된 헬스잇 여성 유저들은 골반 넓히기 보다 다른 목적을 두고 운동을 열심히 하길 바란다.",
            null,
            null)

        private fun commonSenseList(): ArrayList<MultiItemEntity> {
            val commonSenseList = ArrayList<MultiItemEntity>()

            commonSenseParent1.addSubItem(commonSense1)
            commonSenseParent2.addSubItem(commonSense2)
            commonSenseParent3.addSubItem(commonSense3)
            commonSenseParent4.addSubItem(commonSense4)
            commonSenseParent5.addSubItem(commonSense5)
            commonSenseParent6.addSubItem(commonSense6)

            commonSenseList.add(commonSenseParent1)
            commonSenseList.add(commonSenseParent2)
            commonSenseList.add(commonSenseParent3)
            commonSenseList.add(commonSenseParent4)
            commonSenseList.add(commonSenseParent5)
            commonSenseList.add(commonSenseParent6)

            return commonSenseList
        }
        val commonSenseList = commonSenseList()



        private val commonSenseDietParent1 = ParentRoutine("부위별 살 빼는 것이 가능하다?")
        private val commonSenseDiet1 = Routine(
            "부위별 살 빼는 것은 불가능하며 살은 전체적으로 빠지는 것이다. 일부 유튜버들이 부위별 살빼는 운동을 알려준다고 " +
                    "하는데, 당연히 사기다. 유튜버의 운동을 따라하고 실제로 그 부위가 빠졌다고 하는데, 그건 그냥 전체적으로 다 빠진 것이다. " +
                    "이제 진실을 알게 된 헬스잇 유저들은 살을 빼고 싶다면 고강도 유산소 운동을 해보자.",
            null,
            null)

        private val commonSenseDietParent2 = ParentRoutine("다이어트엔 운동? 식이요법?")
        private val commonSenseDiet2 = Routine(
            "다이어트는 음식이 8, 운동이 2란 말이 있을만큼 식이요법이 그 성패를 좌우한다. 아무리 운동을 열심히 해도 보상심리로 " +
                    "고칼로리 음식을 섭취한다면 살이 빠지지 않을 것이다. 살이 잘 빠지지 않는 다면 저칼로리 위주로 식단을 구성해보자. " +
                    "단, 섭취 칼로리를 한 번에 너무 줄이기 보다는 서서히 줄여나가는 것이 다이어트에 효과적이다. 참고로 무작정 굶는 것은 " +
                    "지방뿐 아니라 근육도 많이 빠진다.",
            null,
            null)

        private val commonSenseDietParent3 = ParentRoutine("자기 전에 먹으면 더 살찐다?")
        private val commonSenseDiet3 = Routine(
            "다이어트 하는 사람들은 흔히들 야식을 멀리하려 한다. 하지만 우리 인간은 자는 도중에도 많은 칼로리를 소모한다. " +
                    "대낮에 먹으나 자기전에 먹으나 차이가 없다. 다이어트는 총 섭취 칼로리와 소모 칼로리가 전부다. 언제 먹냐보다는 " +
                    "무엇을 먹는지 신경을 써보자. 그렇지만 자기 전에 먹는 것은 소화기관, 숙면의 질에 안 좋긴 하다.",
            null,
            null)

        private val commonSenseDietParent4 = ParentRoutine("다이어트 보조제는 정말 효과적일까?")
        private val commonSenseDiet4 = Routine(
            "시중엔 수많은 다이어트 보조제가 있고 많은 분들이 이를 섭취한다. 당신이 이미 구매했다면 안타까운 소식이지만 " +
                    "다이어트 보조제의 효과는 아예 없다고 봐도 된다. 무언가를 먹어서 살이 빠진다는 것은 불가능하다고 생각하자. " +
                    "다이어트 보조제 중 섭취하면 배변활동이 원활해져서 효과적이라는 " +
                    "것이 있다. 사실 이 배변활동은 다이어트 보조제의 부작용으로 설사를 하는 게 전부다. 실제로 몸무게가 줄었다고 해도 " +
                    "몸의 수분이나 근육이 빠진 것이지 지방이 빠진 것이 아니다. 가르시니아란 물질이 섭취한 음식물이 몸에 지방으로 저장되는 것을 " +
                    "방해한다고 한다고 홍보하기도 한다. 연구결과 이는 사실이 맞지만 전혀 의미가 없는 수준이다. 지방 0.001g이 축척될 것을 " +
                    "막아주는 정도이니 무시하자. 무언가를 먹으면 살이 빠진다는 것은 정말 꿈만 같은 얘기다. 그럼에도 다이어트 보조제를 " +
                    "구매하겠다면 차라리 그 돈으로 헬스잇 개발자를 후원하자...",
            null,
            null)

        private val commonSenseDietParent5 = ParentRoutine("0칼로리는 정말 살이 안 찐다?")
        private val commonSenseDiet5 = Routine(
            "지금까지 정보 중 듣기 좋은 소식일 것이다. 그렇다. 0칼로리는 정말 살이 아예 안 찐다. 다른 정보에서 말했듯이 " +
                    "살이 찌고 빠지는 것은 칼로리가 전부다. 설탕과 달리 에리스리톨, 스테비아 등 감미료는 몸에 흡수되지 않아 건강에도 해롭지 않고 " +
                    "살이 안 찐다. 0칼로리 음료들은 실제로는 5~8칼로리 정도 되는데, 이 정도 칼로리는 무시하고 마음껏 마셔도 된다. " +
                    "실제로 헬스잇 개발자도 제로콜라와 나랑드사이다를 달고 사니 안심하자.",
            null,
            null)

        private val commonSenseDietParent6 = ParentRoutine("감미료 섭취 후에는 단 음식이 더 땡긴다?")
        private val commonSenseDiet6 = Routine(
            "0칼로리인 감미료로 단맛을 느끼고 나면, 단 음식을 더 갈구하게 된다는 말이 있다. 하지만 이는 전혀 근거가 없는 루머다. " +
                    "실제 연구결과에서도 감미료 섭취 후 식욕이 증가하지 않았다. 감미료 섭취 후 식욕이 더 오른다면, 0칼로리를 섭취했으니 " +
                    "다른 것을 더 먹어도 된다는 합리화를 해버린 것은 아닐지 잘 생각해보자. 참고로 감미료의 부작용으로는 소화불량이 있다. " +
                    "하지만 이 부작용을 경험하려면 하루에 제로콜라 5리터 이상을 마셔야한다니 안심하자. ",
            null,
            null)

        private fun commonSenseDietList(): ArrayList<MultiItemEntity> {
            val commonSenseDietList = ArrayList<MultiItemEntity>()

            commonSenseDietParent1.addSubItem(commonSenseDiet1)
            commonSenseDietParent2.addSubItem(commonSenseDiet2)
            commonSenseDietParent3.addSubItem(commonSenseDiet3)
            commonSenseDietParent4.addSubItem(commonSenseDiet4)
            commonSenseDietParent5.addSubItem(commonSenseDiet5)
            commonSenseDietParent6.addSubItem(commonSenseDiet6)

            commonSenseDietList.add(commonSenseDietParent1)
            commonSenseDietList.add(commonSenseDietParent2)
            commonSenseDietList.add(commonSenseDietParent3)
            commonSenseDietList.add(commonSenseDietParent4)
            commonSenseDietList.add(commonSenseDietParent5)
            commonSenseDietList.add(commonSenseDietParent6)

            return commonSenseDietList
        }
        val commonSenseDietList = commonSenseDietList()

}