package com.mroldl001.goodspack;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class HomeFragment extends Fragment {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private Uri photoUri;

    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<String[]> permissionLauncher;

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        FloatingActionButton plusButton = view.findViewById(R.id.PlusButton);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog();
            }
        });

        // 初始化 ActivityResultLaunchers
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == getActivity().RESULT_OK) {
            }
        });

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == getActivity().RESULT_OK) {
                Uri selectedImage = result.getData().getData();
                if (selectedImage != null) {
                    try {
                        File copiedImageFile = copySelectedImageToGoodsPack(selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                        // TODO 处理复制失败的情况
                    }
                }
            }
        });

        // 请求权限
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
            Boolean cameraPermission = permissions.getOrDefault(Manifest.permission.CAMERA, false);
            Boolean storagePermission = permissions.getOrDefault(Manifest.permission.WRITE_EXTERNAL_STORAGE, false);

            if (cameraPermission && storagePermission) {
                openCamera();
            } else {
                // TODO 处理权限被拒绝的情况
            }
        });

        return view;
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.creating_new_goodspack)
                .setItems(new CharSequence[]{getString(R.string.take_a_picture), getString(R.string.select_file)}, (dialog, which) -> {
                    if (which == 0) {
                        if (hasCameraPermission()) {
                            openCamera();
                        } else {
                            requestCameraPermission();
                        }
                    } else if (which == 1) {
                        openGallery();
                    }
                })
                .show();
    }

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        // 请求相机和存储权限
        permissionLauncher.launch(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
                // TODO 处理文件创建失败的情况
            }
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(getActivity(),
                        "com.mroldl001.goodspack.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

                // 为 Intent 设置权限，以允许其他应用程序访问 URI
                takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                cameraLauncher.launch(takePictureIntent);
            }
        } else {
            // TODO 处理相机无法打开的情况
        }
    }

    private void openGallery() {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(pickPhotoIntent);
    }

    private File createImageFile() throws IOException {
        // 获取 GoodsPack 目录
        File storageDir = getActivity().getExternalFilesDir("GoodsPack");
        if (storageDir != null && !storageDir.exists()) {
            storageDir.mkdirs();
        }

        int fileNumber = 1;
        File imageFile;

        do {
            String imageFileName = fileNumber + ".jpg";
            // 创建文件对象
            imageFile = new File(storageDir, imageFileName);
            fileNumber++;
        } while (imageFile.exists());

        return imageFile;
    }

    private File copySelectedImageToGoodsPack(Uri selectedImageUri) throws IOException {
        // 获取 GoodsPack 目录
        File storageDir = getActivity().getExternalFilesDir("GoodsPack");
        if (storageDir != null && !storageDir.exists()) {
            storageDir.mkdirs();
        }

        int fileNumber = 1;
        File imageFile;

        do {
            String imageFileName = fileNumber + ".jpg";
            // 创建文件对象
            imageFile = new File(storageDir, imageFileName);
            fileNumber++;
        } while (imageFile.exists());

        try (InputStream inputStream = getActivity().getContentResolver().openInputStream(selectedImageUri);
             FileOutputStream outputStream = new FileOutputStream(imageFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        }

        return imageFile;
    }
}
