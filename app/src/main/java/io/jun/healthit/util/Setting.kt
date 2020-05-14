package io.jun.healthit.util

import com.chad.library.adapter.base.entity.MultiItemEntity
import io.jun.healthit.model.ParentRoutine
import io.jun.healthit.model.Routine
import io.jun.healthit.view.MainActivity

//공통으로 사용되는 상수값들 설정
class Setting {
    companion object {
        val IN_KOREA = MainActivity.inKorea
        const val GALLERY_REQUEST_CODE = 0
        const val TAKE_PHOTO_REQUEST_CODE = 1
        const val TOTAL_SIZE_LIMIT = 1160000
        const val IMAGE_SIZE_LIMIT = 9000000
        const val IMAGE_SIZE_LIMIT_MB = IMAGE_SIZE_LIMIT / 1000000
        const val MAX_PHOTO = 7
        const val MAX_RECORD = 50


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
    }
}