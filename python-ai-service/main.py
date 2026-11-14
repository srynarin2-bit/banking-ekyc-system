from fastapi import FastAPI, File, UploadFile, HTTPException
from pydantic import BaseModel
import cv2
import numpy as np
import easyocr
from deepface import DeepFace
import os

app = FastAPI(title="eKYC Pro Banking AI Engine", version="3.0")

print("Initializing EasyOCR...")
reader = easyocr.Reader(['en'], gpu=False)
print("AI Engine Ready!")

class eKYCResponse(BaseModel):
    status: str
    face_match_score: float
    liveness_passed: bool
    extracted_data: dict

@app.post("/api/v1/ai/verify-ekyc", response_model=eKYCResponse)
async def verify_ekyc(idCard: UploadFile = File(...), selfie: UploadFile = File(...)):
    id_path = f"temp_id_{idCard.filename}"
    selfie_path = f"temp_selfie_{selfie.filename}"
    
    try:
        # ១. រក្សាទុករូបភាពជា File បណ្តោះអាសន្នសម្រាប់ DeepFace វិភាគ
        with open(id_path, "wb") as buffer:
            buffer.write(await idCard.read())
        with open(selfie_path, "wb") as buffer:
            buffer.write(await selfie.read())

        # ២. ដំណើរការ OCR ដើម្បីទាញយកទិន្នន័យពីអត្តសញ្ញាណប័ណ្ណ
        ocr_results = reader.readtext(id_path, detail=0)
        full_text = " ".join(ocr_results)

        # ៣. ធ្វើ Real Face Matching រវាងអត្តសញ្ញាណប័ណ្ណ និង រូប Selfie ផ្ទាល់ខ្លួន
        # DeepFace verify នឹងប្រៀបធៀបមុខលើ ID Card និង Selfie ថាតើជាមនុស្សតែមួយដែរឬទេ
        result = DeepFace.verify(img1_path=id_path, img2_path=selfie_path, enforce_detection=False)
        
        distance = result["distance"] # កម្រិតខុសគ្នា (ยิ่งน้อยยิ่งដូច)
        # បំលែង distance ទៅជាเปอร์เซ็นต์ความเหมือน (Similarity Score)
        match_score = round(max(0.0, (1.0 - distance) * 100), 2)
        
        # កំណត់កម្រិត περα πέρα (Threshold) ឧទាហរណ៍ លើសពី 55% ถือว่าผ่าน
        is_matched = result["verified"] and match_score >= 50.0

        # ៤. Liveness Simulation (អាចពង្រីកបន្ថែមដោយ Blink detection)
        liveness_passed = True 

        extracted_data = {
            "raw_ocr_text": ocr_results[:6],
            "note": "Verified using DeepFace & EasyOCR AI"
        }

        return {
            "status": "APPROVED" if is_matched and liveness_passed else "REJECTED",
            "face_match_score": match_score,
            "liveness_passed": liveness_passed,
            "extracted_data": extracted_data
        }

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
    finally:
        # លុប File បណ្តោះអាសន្នចេញពី Disk វិញដើម្បីសន្សំ Memory
        if os.path.exists(id_path): os.remove(id_path)
        if os.path.exists(selfie_path): os.remove(selfie_path)