import { configureStore } from '@reduxjs/toolkit';

const store = configureStore({
    reducer: {
       
        namozVaqtlari: namozVaqtlariReducer,

    },
    middleware: (getDefaultMiddleware) =>
        getDefaultMiddleware({
            serializableCheck: {

                ignoredActions: ['spirituality/setFormValues','topics/setSelectedFile','informationCenter/setSelectedFile','audioMaterial/setSelectedFile','holyPlaces/setSelectedFile','acceptance/setSelectedFile','departments/setSelectedFile','graduateStudents/setSelectedFile' , 'professors/setImage', "videos/setVideoFile",'spirituality/setFormValues','gazetalar/setSelectedFile','gazetalar/setSelectedPdf','MuminalarJurnali/setSelectedFile','MuminalarJurnali/setSelectedPdf','hidoyatJurnali/setSelectedFile','hidoyatJurnali/setSelectedPdf','kitoblar/setSelectedFile','kitoblar/setSelectedPdf',"videos/setVideoFile",'topics/setSelectedFile',"newsHead/setSelectedFile",'informationCenter/setSelectedFile','audioMaterial/setSelectedFile','holyPlaces/setSelectedFile','acceptance/setSelectedFile','departments/setSelectedFile','graduateStudents/setSelectedFile',
                    'liveOfMadrasa/setSelectedFile','liveOfMadrasa/setSelectedFileTwo'
                    ,'school/setSelectedFile','teachers/setSelectedFile','kafedra/setSelectedFile',"monuments/setSelectedFile", 'users/setFormImage',"news/setFormImage"],
                ignoredPaths: ['topics.selectedFile','spirituality.form','informationCenter.selectedFile','audioMaterial.selectedFile','holyPlaces.selectedFile','acceptance.selectedFile','departments.selectedFile','graduateStudents.selectedFile', 'professors.image', "videos.videoFile",
                    'liveOfMadrasa.selectedFile','news.form.image' ,"users.image", 'liveOfMadrasa.selectedFileTwo'
                    ,'school.selectedFile','kafedra.selectedFile','school/setSelectedFile','kafedra/setSelectedFile',
                    "monuments.selectedFileName","professorTeacher/setForm",'topics.selectedFile','spirituality.form',"videos.videoFile",'informationCenter.selectedFile',"newsHead.selectedFile",'audioMaterial.selectedFile','holyPlaces.selectedFile','acceptance.selectedFile','departments.selectedFile','graduateStudents.selectedFile',
                    'liveOfMadrasa.selectedFile','liveOfMadrasa.selectedFileTwo'
                    ,'teachers.selectedFile','school.selectedFile','kafedra.selectedFile','gazetalar.selectedFile','gazetalar.selectedPdf','MuminalarJurnali.selectedFile','MuminalarJurnali.selectedPdf','hidoyatJurnali.selectedFile','hidoyatJurnali.selectedPdf','kitoblar.selectedFile','kitoblar.selectedPdf',"professorTeacher.form.img"]


            },
        }),
});

export default store;